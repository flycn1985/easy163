package org.ndroi.easy163.providers;

import org.ndroi.easy163.providers.utils.BitRate;
import org.ndroi.easy163.providers.utils.ReadStream;
import org.ndroi.easy163.utils.Keyword;
import org.ndroi.easy163.utils.Song;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public abstract class Provider
{
    static protected String keyword2Query(Keyword keyword)
    {
        String str = keyword.songName;
        for (String singer : keyword.singers)
        {
            str += (" " + singer);
        }
        try
        {
            str = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return str;
    }

    static protected Song generateSong(String url)
    {
        Song song = null;
        try
        {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("range", "bytes=0-8191");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK ||
                    responseCode == HttpURLConnection.HTTP_PARTIAL)
            {
                song = new Song();
                song.url = url;
                String content_range = connection.getHeaderField("Content-Range");
                if (content_range != null)
                {
                    int p = content_range.indexOf('/');
                    song.size = Integer.parseInt(content_range.substring(p + 1));
                } else
                {
                    song.size = connection.getContentLength();
                }
                String qqMusicMd5 = connection.getHeaderField("Server-Md5");
                if (qqMusicMd5 != null)
                {
                    song.md5 = qqMusicMd5;
                }
                byte[] mp3Data = ReadStream.read(connection.getInputStream());
                song.br = BitRate.Detect(mp3Data);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return song;
    }

    abstract public Song match(Keyword keyword);
}
