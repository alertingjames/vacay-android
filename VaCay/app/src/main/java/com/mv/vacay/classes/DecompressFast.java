package com.mv.vacay.classes;

/**
 * Created by a on 1/31/2017.
 */

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.mv.vacay.commons.Commons;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DecompressFast {

    private String _zipFile;
    private String _location;
    Context _context;

    public DecompressFast(Context context, String zipFile, String location) {
        _zipFile = zipFile;
        _location = location;
        _context=context;

        _dirChecker("");
    }

    public boolean unzip() {
        try  {
            FileInputStream fin = new FileInputStream(_zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                Log.v("Decompress", "Unzipping " + ze.getName());

                if(ze.isDirectory()) {
                    _dirChecker(ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(_location + ze.getName());
                    BufferedOutputStream bufout = new BufferedOutputStream(fout);
                    byte[] buffer = new byte[1024];
                    int read = 0;
                    while ((read = zin.read(buffer)) != -1) {
                        bufout.write(buffer, 0, read);
                    }

                    Toast.makeText(_context,"Successfully decompressed.", Toast.LENGTH_SHORT).show();
                    Commons.gameEntity.setVideoId(_location + ze.getName());

                    bufout.close();

                    zin.closeEntry();
                    fout.close();
                }

            }
            zin.close();
            Log.d("Unzip", "Unzipping complete. path :  " +_location );
            return true;
        } catch(Exception e) {
            Log.e("Decompress", "unzip", e);

            Log.d("Unzip", "Unzipping failed");
        }
        return false;

    }

    private void _dirChecker(String dir) {
        File f = new File(_location + dir);

        if(!f.isDirectory()) {
            f.mkdirs();
        }
    }


}
