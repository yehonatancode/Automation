package Pages;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class TableHandler {

    public static final String MISSING_COLUMN_MSG = "Error, search or return column aren't found.";
    public static final String VALUE_MISSING_ERR_MSG = "Value missing from the table";
    public static final String GECKO_DRIVER = "webdriver.gecko.driver";
    public static final String DRIVER_PATH = "/Users/yonix/Downloads/geckodriver";
    public static final String TABLE_URL = "https://www.w3schools.com/html/html_tables.asp";
    public static final String TABLE_XPATH = "//*[@id=\"customers\"]";
    public static final String INVALID_INDEX_MSG = "Invalid return column index.";
    public static final String MISSING_TEXT_MSG = "Search text not found in the table.";
    static WebDriver driver;
    private static final String PROPERTIES_FILE_PATH = "/Users/yonix/IdeaProjects/testnetExam/src/table_content.properties";


    public static String getTableCellText(WebElement table, int searchColumn, String searchText, int returnColumnText) {
        String returnValue = null;

        // Get all rows in the table
        List<WebElement> rows = table.findElements(By.tagName("tr"));

        // Find the index of the columns
        List<WebElement> headers = rows.get(0).findElements(By.tagName("th"));
        int searchColumnIndex = -1;

        // Find the index of the search column
        for (int i = 0; i < headers.size(); i++) {
            String headerText = headers.get(i).getText().trim();

            if (headerText.equalsIgnoreCase(searchText) || i == searchColumn) {
                searchColumnIndex = i;
                break;
            }
        }

        // Check if the search column was found
        if (searchColumnIndex == -1) {
            throw new IllegalArgumentException(MISSING_COLUMN_MSG);
        }

        // Loop through each row and find the matching cell in the search column
        for (int i = 1; i < rows.size(); i++) {
            List<WebElement> cells = rows.get(i).findElements(By.tagName("td"));
            String cellText = cells.get(searchColumnIndex).getText().trim();

            if (cellText.equalsIgnoreCase(searchText)) {
                // Check if the return column index is within the valid range
                if (returnColumnText >= 0 && returnColumnText < cells.size()) {
                    returnValue = cells.get(returnColumnText).getText().trim();
                    break;
                } else {
                    throw new IllegalArgumentException(INVALID_INDEX_MSG);
                }
            }
        }

        // Check if a match was found in the search column
        if (returnValue == null) {
            throw new IllegalArgumentException(MISSING_TEXT_MSG);
        }

        return returnValue;
    }


        public static boolean verifyTableCellText(WebElement table, int searchColumn, String searchText,
                                                  int returnColumnText, String expectedText) {
            String actualText = getTableCellText(table, searchColumn, searchText, returnColumnText);
            return actualText.equals(expectedText);
        }



    public static String getTableCellTextByXpath(WebElement table, int searchColumn, String searchText, int returnColumnText) throws IOException {
        Properties properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream(PROPERTIES_FILE_PATH);
        properties.load(fileInputStream);
        fileInputStream.close();

        String cellText = "";

        //Seek in property file, and match XPath's Regex to create valid XPath expression
        String searchXPath = "//*[@id=\"customers\"]/tbody/tr/td[" + (searchColumn + 1) + "][contains(text(), '%s')]";
        String returnXPath = "//*[@id=\"customers\"]/tbody/tr/td[" + (returnColumnText + 1) + "]";

        String searchTextValue = properties.getProperty(searchText);
        if (searchTextValue != null) {
            String searchColumnXpath = String.format(searchXPath, searchTextValue);
            String returnColumnXpath = String.format(returnXPath, returnColumnText);

            WebElement searchCell = table.findElement(By.xpath(searchColumnXpath));
            if (searchCell != null) {
                WebElement returnCell = searchCell.findElement(By.xpath(returnColumnXpath));
                if (returnCell != null) {
                    cellText = returnCell.getText().trim();
                }
            }
        }

        if (cellText.isEmpty()) {
            throw new RuntimeException(VALUE_MISSING_ERR_MSG);
        }

        return cellText;
    }





        public static void main(String[] args) {
            System.setProperty(GECKO_DRIVER, DRIVER_PATH);

            driver = new FirefoxDriver();
            driver.get(TABLE_URL);

            WebElement table = driver.findElement(By.xpath(TABLE_XPATH));

            // Sanity basic test.
            String companyName = "Ernst Handel";
            String countryName = getTableCellText(table, 0, companyName, 2);
            boolean assertion = verifyTableCellText(table, 0, companyName, 2, "Austria");
            System.out.println(countryName+ " , " + assertion);
//
            //check column 2 to 3
            String contactName = "Helen Bennett";
            String helensCountryName = getTableCellText(table, 1, contactName, 2);
            boolean assertion2 = verifyTableCellText(table, 1, contactName, 2, "UK");


//check column 3 to 1

            String otherCountryName = "Canada";
            String canadasCompanyName = getTableCellText(table, 2, otherCountryName, 0);
            boolean assertion3 = verifyTableCellText(table, 2, otherCountryName, 0, "Laughing Bacchus Winecellars");
            System.out.println(canadasCompanyName+ " , " + assertion3);

        try {
            String searchText = "company1";
            int searchColumn = 0; // Assuming the company column is the first column (0-based index)
            int returnColumnText = 2; // Assuming the country column is the third column (0-based index)

            String country = getTableCellTextByXpath(table, searchColumn, searchText, returnColumnText);
            System.out.println("Country for Company '" + searchText + "': " + country);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }



            driver.quit();
        }
    }


