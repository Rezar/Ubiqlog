package com.ubiqlog.utils;

/**
 * Created by Ping_He on 2016/1/15.
 */
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

// this class provide the tool to compress the file,
// the path is the path(including the file name) of source file need to be compressed
// outputpath is the path(including the file name) of output file
public class Compresstool {

    public static void zipcompress(String path,String outputpath)
    {

        try
        {
            File file = new File(path);
            File zipfile = new File(outputpath);
            InputStream input = new FileInputStream(file);
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipfile));
            zipOut.putNextEntry(new ZipEntry(file.getName()));
            int temp;
            while((temp = input.read()) != -1){
                zipOut.write(temp);
            }
            input.close();
            zipOut.flush();
            zipOut.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void deflatercompress(String path,String outputpath)
    {
        try
        {	//transfer file to byte[]
            File file = new File(path);
            InputStream input = new FileInputStream(file);
            byte[] inputbyte = new byte[input.available()];
            input.read(inputbyte);

            Deflater compressor = new Deflater();
            compressor.setLevel(Deflater.BEST_COMPRESSION);
            compressor.setInput(inputbyte);
            compressor.finish();
            // Compress the data
            final byte[] buf = new byte[1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while (!compressor.finished()) {
                int count = compressor.deflate(buf);
                bos.write(buf, 0, count);
            }
            //write  ByteArrayOutputStream to file
            BufferedOutputStream bufferedOutput = new BufferedOutputStream(new FileOutputStream(new File(outputpath)));
            bufferedOutput.write(bos.toByteArray());
            input.close();
            compressor.end();
            bos.close();
            bufferedOutput.flush();
            bufferedOutput.close();

        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void inflatordecompress(String path,String outputpath)
    {
        try
        {	//transfer file to byte[]
            File file = new File(path);
            InputStream input = new FileInputStream(file);
            byte[] inputbyte = new byte[input.available()];
            input.read(inputbyte);

            Inflater inflator= new Inflater();
            inflator.setInput(inputbyte);
            //decompress data
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while (!inflator.finished()) {
                int count = inflator.inflate(buffer);
                bos.write(buffer, 0, count);
            }

            //write  ByteArrayOutputStream to file
            BufferedOutputStream bufferedOutput = new BufferedOutputStream(new FileOutputStream(new File(outputpath)));
            bufferedOutput.write(bos.toByteArray());

            input.close();
            inflator.end();
            bos.close();
            bufferedOutput.flush();
            bufferedOutput.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void Gzipcompress(String path,String outputpath)
    {
        try{
            File file = new File(path);
            InputStream input = new FileInputStream(file);
            GZIPOutputStream out =new GZIPOutputStream(new FileOutputStream(outputpath));
            byte[] buf = new byte[1024];
            int len;
            while((len = input.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            input.close();
            out.flush();
            out.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void Gzipdecompress(String path,String outputpath)
    {
        try{
            GZIPInputStream in =new GZIPInputStream(new FileInputStream(path));
            FileOutputStream out = new FileOutputStream(outputpath);
            byte[] buf = new byte[1024];
            int len;
            while((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.flush();
            out.close();

        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public static void BZip2compress(String path,String outputpath)
    {
        try{

            InputStream input = new FileInputStream(new File(path));
            BZip2CompressorOutputStream out = new BZip2CompressorOutputStream(new FileOutputStream(outputpath));

            int count;
            byte[] buf = new byte[1024];
            int len;
            while((len = input.read(buf))!=-1) {
                out.write(buf, 0, len);
            }
            out.finish();

            out.flush();
            out.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}
