package com.chernandezgil.farmacias.supercsv;

import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.model.FarmaciasCsvBean;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carlos on 09/07/2016.
 */
public class CsvReader {

    private static final String LOG_TAG=CsvReader.class.getSimpleName();
    //   https://www.dropbox.com/s/lo5nrm1inmgh27o/farmacias_extremadura.txt?dl=0
//https://dl.dropboxusercontent.com/u/19281611/Farmacias/farmacias_extremadura.txt
    //private static String endpoint="https://dl.dropboxusercontent.com/s/lo5nrm1inmgh27o/farmacias_extremadura.txt";
    //https://dl.dropboxusercontent.com/u/19281611/Farmacias/farmacias_extremadura.txt
    private static String endpoint = "https://dl.dropboxusercontent.com/u/19281611/Farmacias/farmacias_extremadura.txt";

    private static CellProcessor[] getProcessors() {
        final CellProcessor[] processors = new CellProcessor[]{
                new ParseInt(), // id (must be unique)
                new NotNull(), // address
                new NotNull(), // horario
                new ParseDouble(), //lat
                new ParseDouble(), //long
                new NotNull(), // name
                new Optional(), // phone
                new Optional(), // locality
                new Optional(), // province
                new Optional(), // postal_code

        };
        return processors;
    }

    public static List<FarmaciasCsvBean> readWithCsvBeanReader() {
        ICsvBeanReader beanReader = null;

        try {
            URL url = new URL(endpoint);
            InputStream is = url.openStream();
            beanReader = new CsvBeanReader(new InputStreamReader(is), CsvPreference.TAB_PREFERENCE);
            // the header elements are used to map the values to the bean (names must match)
            final String[] header = beanReader.getHeader(true);
            final CellProcessor[] processors = getProcessors();
            List<FarmaciasCsvBean> listaFarmacias = new ArrayList<>();
            FarmaciasCsvBean farmacias;

            while ((farmacias = beanReader.read(FarmaciasCsvBean.class, header, processors)) != null) {
                listaFarmacias.add(farmacias);


            }
            return listaFarmacias;
        } catch (Exception e) {
            Util.LOGD(LOG_TAG,"error:" + e.getMessage());
            return null;
        } finally {
            if (beanReader != null) {
                try {
                    beanReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
