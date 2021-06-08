package com.example.persistancedonneefichier;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    /*
        //Fichier d'une application
        File outFile = getExternalFilesDir(Environment.DIRECTORY_DCIM);

        //Fichier partagé
        File sharedFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
     */

    EditText editText;
    TextView textView;
    Button btnSave;
    Button btnShow;
    RadioGroup saveOption;
    //Choix de la sauvegarde (cache, fichier, etc)
    int nSaveOption = -1;

    //Nom du fichier
    final static String FICHIER = "Fichier.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText)findViewById(R.id.editText);
        textView = (TextView)findViewById(R.id.textView);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnShow = (Button)findViewById(R.id.btnShow);
        saveOption = (RadioGroup)findViewById(R.id.saveOption);

        saveOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group.getCheckedRadioButtonId() == R.id.cacheButton) {
                    nSaveOption = 0;
                } else if (group.getCheckedRadioButtonId() == R.id.sharedButton) {
                    nSaveOption = 1;
                } else if (group.getCheckedRadioButtonId() == R.id.fichierApplication) {
                    nSaveOption = 2;
                } else {
                    nSaveOption = 3;
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFile();
            }
        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile();
            }
        });

    }

    void saveFile() {
        //Sauvegarder du texte dans un fichier
        String savedText = editText.getText().toString();
        try {
            FileOutputStream fileOutputStream;
            //Ouverture du fichier selon l'option de sauvegarde choisi
            switch (nSaveOption) {
                case 0: //Cache
                    fileOutputStream = new FileOutputStream(new File(getCacheDir(),FICHIER));
                    break;
                case 1: //Fichier partagé
                    if (isExternalStorageWritable()) {
                        File outFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        fileOutputStream = new FileOutputStream(new File(outFile,FICHIER));
                    } else {
                        fileOutputStream = openFileOutput(FICHIER,Context.MODE_PRIVATE);
                    }
                    break;
                case 2: //Fichier de l'application
                    if (isExternalStorageWritable()) {
                        File outFile = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                        fileOutputStream = new FileOutputStream(new File(outFile,FICHIER));
                    } else {
                        fileOutputStream = openFileOutput(FICHIER,Context.MODE_PRIVATE);
                    }
                    break;
                case 3:
                default:
                    fileOutputStream = openFileOutput(FICHIER,Context.MODE_PRIVATE);
                    break;
            }
            fileOutputStream.write(savedText.getBytes()); //Ecriture dans le fichier
            fileOutputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    void openFile() {
        //Afficher du texte contenu dans un fichier
        try {
            FileInputStream fileInputStream;
            //Ouverture du fichier selon l'option de sauvegarde choisi
            switch (nSaveOption) {
                case 0: //Cache
                    fileInputStream = new FileInputStream(new File(getCacheDir(),FICHIER));
                    break;
                case 1: //Fichier partagé
                    if (isExternalStorageWritable()) {
                        File inFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        fileInputStream = new FileInputStream(new File(inFile,FICHIER));
                    } else {
                        fileInputStream = openFileInput(FICHIER);
                    }
                    break;
                case 2: //Fichier de l'application
                    if (isExternalStorageWritable()) {
                        File inFile = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                        fileInputStream = new FileInputStream(new File(inFile,FICHIER));
                    } else {
                        fileInputStream = openFileInput(FICHIER);
                    }
                    break;
                case 3:
                default:
                    fileInputStream = openFileInput(FICHIER);
                    break;
            }

            byte[] buffer = new byte[1024]; //se remplit à chaque lecture
            StringBuilder contenu = new StringBuilder(); //Contenu du fichier

            //Lecture du fichier
            while((fileInputStream.read(buffer)) != -1) {
                contenu.append(new String(buffer));
            }
            fileInputStream.close();
            textView.setText(contenu);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Stockage : permission d'écrire
    boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    //Stockage : permission de lire
    boolean isExternalStorageReadable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }
}