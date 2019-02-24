package cn.chenhuanming.octopus;

import cn.chenhuanming.octopus.config.ConfigFactory;
import cn.chenhuanming.octopus.config.XmlConfigFactory;
import cn.chenhuanming.octopus.exception.SheetNotFoundException;
import cn.chenhuanming.octopus.model.CellPosition;
import cn.chenhuanming.octopus.model.CheckedData;
import cn.chenhuanming.octopus.reader.CheckedExcelReader;
import cn.chenhuanming.octopus.reader.DefaultExcelReader;
import cn.chenhuanming.octopus.reader.ExcelReader;
import cn.chenhuanming.octopus.reader.SheetReader;
import cn.chenhuanming.octopus.writer.DefaultExcelWriter;
import cn.chenhuanming.octopus.writer.DefaultSheetWriter;
import cn.chenhuanming.octopus.writer.ExcelWriter;
import cn.chenhuanming.octopus.writer.SheetWriter;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * help you for simply import or output excel
 *
 * @author chenhuanming
 * Created at 2018/12/20
 * @see ExcelReader
 * @see SheetReader
 * @see ExcelWriter
 * @see SheetWriter
 */
public final class Octopus {
    /**
     * read config from XML file
     *
     * @param is XML file
     * @return configFactory
     */
    public static ConfigFactory getXMLConfigReader(InputStream is) {
        return new XmlConfigFactory(is);
    }

    /**
     * write one sheet into excel file
     *
     * @param os           excel file
     * @param configFactory get configFactory from @{{@link #getXMLConfigReader(InputStream)}}
     * @param sheetName    name of sheet
     * @param data         data
     * @throws IOException when writing excel file failed
     */
    public static <T> void writeOneSheet(OutputStream os, ConfigFactory configFactory, String sheetName, Collection<T> data) throws IOException {
        ExcelWriter writer = new DefaultExcelWriter(new SXSSFWorkbook(), os);
        writer.write(sheetName, new DefaultSheetWriter<T>(configFactory), data);
        writer.close();
    }

    /**
     * read data from first sheet of excel
     *
     * @param is            excel file
     * @param configFactory  get configFactory from @{{@link #getXMLConfigReader(InputStream)}}
     * @param startPosition where to start read,starting from 0
     * @param <T>           class type of data you want
     * @return data
     * @throws IOException                if an error occurs while reading the data
     * @throws InvalidFormatException     if the contents of the file cannot be parsed into a {@link Workbook}
     * @throws EncryptedDocumentException If the workbook given is password protected
     * @see cn.chenhuanming.octopus.model.DefaultCellPosition
     */
    public static <T> SheetReader<T> readFirstSheet(InputStream is, ConfigFactory configFactory, CellPosition startPosition) throws IOException, InvalidFormatException, EncryptedDocumentException {
        return readOneSheet(is, 0, configFactory, startPosition);
    }

    /**
     * read data from sheet at the specified position in excel
     *
     * @param is            excel file
     * @param index         position,starting from 0
     * @param configFactory  get configFactory from @{{@link #getXMLConfigReader(InputStream)}}
     * @param startPosition where to start read,starting from 0
     * @param <T>           class type of data you want
     * @return data
     * @throws IOException                if an error occurs while reading the data
     * @throws InvalidFormatException     if the contents of the file cannot be parsed into a {@link Workbook}
     * @throws EncryptedDocumentException If the workbook given is password protected
     */
    public static <T> SheetReader<T> readOneSheet(InputStream is, int index, ConfigFactory configFactory, CellPosition startPosition) throws IOException, InvalidFormatException, EncryptedDocumentException {
        Workbook workbook = WorkbookFactory.create(is);
        return new DefaultExcelReader<T>(workbook).get(index, configFactory, startPosition);
    }

    /**
     * read data from sheet by name in excel
     *
     * @param is            excel file
     * @param sheetName     name of sheet in the excel
     * @param configFactory  get configFactory from @{{@link #getXMLConfigReader(InputStream)}}
     * @param startPosition where to start read,starting from 0
     * @param <T>           class type of data you want
     * @return data
     * @throws IOException                if an error occurs while reading the data
     * @throws InvalidFormatException     if the contents of the file cannot be parsed into a {@link Workbook}
     * @throws EncryptedDocumentException If the workbook given is password protected
     * @throws SheetNotFoundException     when none of sheets'name is <code>sheetName</code>
     */
    public static <T> SheetReader<T> readBySheetName(InputStream is, String sheetName, ConfigFactory configFactory, CellPosition startPosition) throws IOException, InvalidFormatException, EncryptedDocumentException, SheetNotFoundException {
        Workbook workbook = WorkbookFactory.create(is);
        return new DefaultExcelReader<T>(workbook).get(sheetName, configFactory, startPosition);
    }

    public static <T> SheetReader<CheckedData<T>> readFirstSheetWithValidation(InputStream is, ConfigFactory configFactory, CellPosition startPosition) throws IOException, InvalidFormatException, EncryptedDocumentException {
        return readOneSheetWithValidation(is, 0, configFactory, startPosition);
    }

    public static <T> SheetReader<CheckedData<T>> readOneSheetWithValidation(InputStream is, int index, ConfigFactory configFactory, CellPosition startPosition) throws IOException, InvalidFormatException, EncryptedDocumentException {
        Workbook workbook = WorkbookFactory.create(is);
        return new CheckedExcelReader<T>(workbook).get(index, configFactory, startPosition);
    }

    public static <T> SheetReader<CheckedData<T>> readBySheetNameWithValidation(InputStream is, String sheetName, ConfigFactory configFactory, CellPosition startPosition) throws IOException, InvalidFormatException, EncryptedDocumentException, SheetNotFoundException {
        Workbook workbook = WorkbookFactory.create(is);
        return new CheckedExcelReader<T>(workbook).get(sheetName, configFactory, startPosition);
    }
}
