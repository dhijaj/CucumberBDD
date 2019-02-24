package Utilities;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;


import org.junit.*;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.commons.configuration2.PropertiesConfiguration;
//import org.apache.commons.lang.RandomStringUtils;
//import org.apache.log4j.Category;
//import org.apache.logging.log4j.PropertyConfigurator;
import cucumber.api.Scenario;


public class WebDriverUtility {

    public static WebDriver driver;
    public static PropertiesConfiguration envProperty;
    public static String  url, browser, chromeDriverPath,
                    IeDriverPath;
    private List<WebElement> elements;
    public static WebDriverWait wait;
    public Select select;
    private static FirefoxProfile profile;
    final String PropertyPath="./src/main/config/config.properties";
    final String LOG_PROPERTIES_FILE="./src/main/config/Log4j.properties";
    final int timeoutSec=5;




    public WebDriverUtility() {

        if(driver==null){

                envProperty=new PropertiesConfiguration();
                url=envProperty.getString("URL");
                browser = envProperty.getString("BROWSER");
                chromeDriverPath = envProperty.getString("chromeDriverPath");
                IeDriverPath = envProperty.getString("IeDriverPath");

                try {

                    if (browser.equals("chrome")) {
                        File file = new File(chromeDriverPath);
                        driver = new ChromeDriver();
                    } else if (browser.equals("IE")) {
                        File file = new File(IeDriverPath);
                        driver = new InternetExplorerDriver();
                    } else if (browser.equals("Safari"))
                        driver = new SafariDriver();

                    driver.get(url);
                }
                catch(NoSuchElementException e)
                {
                    throw new RuntimeException("Unable to load Webdriver");
                }

                finally{
                Runtime.getRuntime().addShutdownHook(
                        new Thread(new BrowserCleanup()));
            }

        }
        //check if the driver has already exit, if it has then stopping the build.

    }

    //close browser when the test finish
    private static class BrowserCleanup implements Runnable {
        public void run() {
            close();
        }
    }


    public static void close() {
        try {
            driver.close();
            driver.quit();
        } catch (UnreachableBrowserException e) {
        }
    }


    public String getTitle(){
        return driver.getTitle();
    }



    public void deleteCookies(){
        driver.manage().deleteAllCookies();
    }

    public  WebDriverWait waitForElement() throws Exception
    {
        try
        {

            wait=new WebDriverWait(driver,40);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return wait;
    }



    public void waitForPageToLoad()
    {
        (new WebDriverWait(driver, 40)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return (((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
            }
        });
    }



    public boolean waitForElementToBePresent(By by) throws Exception
    {
        return waitForElement().until(ExpectedConditions.presenceOfElementLocated(by)).isDisplayed();
    }


    public boolean waitForElementToDisappear(By by) throws Exception
    {
        return waitForElement().until(ExpectedConditions.invisibilityOfElementLocated(by));
    }


    public boolean waitForElementToBeClickable(By by) throws Exception
    {
        return waitForElement().until(ExpectedConditions.elementToBeClickable(by)).isDisplayed();
    }




    public boolean isTextPresent(String text) throws Exception
    {

        try{
            Thread.sleep(2000);
            boolean b = driver.getPageSource().contains(text);
            return b;
        }
        catch(Exception e){
            return false;
        }
    }



    public boolean checkIfElementIsPresent(By by) throws Exception
    {
        boolean ret=false;
        try
        {

            ret=waitForElement().until(ExpectedConditions.visibilityOfElementLocated(by)).isDisplayed();
            if(ret)
            {
                List<WebElement> elements=driver.findElements(by);

                if(elements.size()>0)
                {
                    //log.debug("element size"+elements.size());
                    ret=true;
                }
                else
                {
                    ret=false;

                }
            }
        }
        catch(NoSuchElementException e)
        {
            Assert.assertEquals(ret,"The element is not found in web page");
            e.printStackTrace();
        }
        return ret;

    }



    public boolean checkIfElementNotPresent(By by) throws Exception
    {
        boolean ret=false;
        try
        {

            ret=waitForElement().until(ExpectedConditions.invisibilityOfElementLocated(by));
        }

        catch(NoSuchElementException e)
        {

        }
        return ret;

    }




    public List<WebElement> getAllElementsByLocator(By by) throws Exception
    {
        List<WebElement> elements = null;

            waitForElement().until(ExpectedConditions.elementToBeClickable(by));
            elements=driver.findElements(by);

        return elements;
    }



    //get all elements without checking if its clickable
    public List<WebElement> getAllElements(By by) throws Exception
    {
        List<WebElement> elements = null;
        elements=driver.findElements(by);
        return elements;
    }

    public List<WebElement> getAllChildElements(By Parent, By by) throws Exception
    {

        List<WebElement> elements = null;
        WebElement parent = driver.findElement(Parent);
        elements=parent.findElements(by);
        return elements;
    }





    public boolean clickOnElementofListofElements(By by,String menuName) throws Exception
    {
        boolean ret=false;

        try
        {
            elements=getAllElementsByLocator(by);
            //elements=getAllElements(by);
            //log.debug("Elements size" + elements.size());

            if(elements.size()>0)
            {
                for(WebElement options:elements)
                {

                    //log.debug(options.getText() + "button");
                    if(options.getText().toLowerCase().trim().contains(menuName.toLowerCase()))
                    {

                        Thread.sleep(1000);
                        // log.debug("found");
                        options.click();

                        ret=true;
                        break;
                    }
                }
            }

        }
        catch(Exception e)
        {
        }
        return ret;
    }



    public boolean clickOnChildElementofListofElements(By by,String menuName, By child) throws Exception
    {
        boolean ret=false;
        elements = getAllElementsByLocator(by);
        try
        {


            if(elements.size()>1)
            {
                for(WebElement options:elements)
                {

                    //log.debug(options.getText() + "button");
                    if(options.getText().toLowerCase().trim().contains(menuName.toLowerCase()))
                    {
                        options.findElement(child).click();

                        ret=true;
                        break;
                    }
                }
            }
            else
                elements.get(0).findElement(child).click();

        }
        catch(Exception e)
        {
        }
        return ret;
    }



    public boolean clickOnChildElement(WebElement element, By child) throws Exception
    {
        boolean ret=false;

        try
        {

            element.findElement(child).click();

            ret=true;


        }
        catch(Exception e)
        {
        }
        return ret;
    }


    public boolean EnterTextOnChildElement(By by, By child, String text) throws Exception
    {
        boolean ret=false;

        try
        {
            WebElement element = driver.findElement(by);
            element.findElement(child).click();
            element.findElement(child).sendKeys(text);
            ret = true;

        }
        catch(Exception e){}
        return ret;


    }


    public int getcountofElementsFound(By by)throws Exception
    {
        int count = 0;
        boolean ret=false;
        try
        {
            ret=waitForElement().until(ExpectedConditions.visibilityOfElementLocated(by)).isDisplayed();

            if(ret)
            {
                count=driver.findElements(by).size();
            }
        }

        catch(NoSuchElementException e)
        {
            Assert.assertEquals(ret,"The element is not found in web page");
            e.printStackTrace();
        }
        return count;
    }



    public String getTextOfElement(By by) throws Exception
    {
        String textValue=null;
        boolean ret=false;
        try
        {


            if(waitForElement().until(ExpectedConditions.visibilityOfElementLocated(by)).isDisplayed())
            {
                List<WebElement> elements=driver.findElements(by);

                if(elements.size()>=1)
                {

                    textValue=driver.findElements(by).get(0).getText().trim();
                    ret=true;

                }
            }
        }
        catch(NoSuchElementException e)
        {
            Assert.assertEquals(ret,"The element is not found in web page");
            e.printStackTrace();
        }
        return textValue;

    }


    public String getCssValueOfElement(By by, String css) throws Exception
    {
        String textValue=null;
        boolean ret=false;
        try
        {


            if(waitForElement().until(ExpectedConditions.visibilityOfElementLocated(by)).isDisplayed())
            {
                textValue=driver.findElement(by).getCssValue(css);
                ret=true;

            }

        }
        catch(NoSuchElementException e)
        {
            Assert.assertEquals(ret,"The element is not found in web page");
            e.printStackTrace();
        }
        return textValue;

    }



    public String getAttributeValueOfElement(By by,String attributeName) throws Exception
    {
        boolean ret=false;
        String attributeValue=null;
        try
        {
            if(checkIfElementIsPresent(by))
            {
                ret=true;
                attributeValue=driver.findElement(By.cssSelector("input[type='text'][id='users-id-search'][name='users-id-search']")).getAttribute(attributeName.trim()).trim();
            }
        }
        catch(NoSuchElementException e)
        {
            Assert.assertEquals(ret,"The element is not found in web page");
            e.printStackTrace();
        }
        return attributeValue;
    }







    public boolean clickElementByLocator(By by) throws Exception
    {
        boolean ret = false;

        try
        {
            ret=waitForElement().until(ExpectedConditions.visibilityOfElementLocated(by)).isDisplayed();


            if(ret)
            {
                ret=true;
                //if(browser.equals("Chrome")){
                WebElement element = driver.findElement(by);
                Actions actions = new Actions(driver);
                actions.moveToElement(element).click().perform();
                //}

                //else
                //driver.findElement(by).click();
            }
        }

        catch(NoSuchElementException e)
        {
            Assert.assertEquals(ret,"No such element found in Page");
            e.printStackTrace();
        }
        return ret;
    }


    public boolean clickElementByElement(WebElement element) throws Exception
    {
        boolean ret = false;



        try{
            //if(browser.equals("Chrome")){

            Actions actions = new Actions(driver);
            actions.moveToElement(element).click().perform();
            ret=true;

        }

        catch(NoSuchElementException e)
        {
            Assert.assertEquals(ret,"No such element found in Page");
            e.printStackTrace();
        }
        return ret;
    }


    public boolean enterValueInToTextField(By by,String textValue) throws Exception
    {
        boolean ret = false;

        try
        {
            ret=waitForElement().until(ExpectedConditions.visibilityOfElementLocated(by)).isDisplayed();

            driver.findElement(by).clear();
            driver.findElement(by).sendKeys(textValue);

        }

        catch(NoSuchElementException e)
        {
            Assert.assertEquals(ret,"No such element found in Page");
            e.printStackTrace();
        }
        return ret;
    }



    //send the press keyboard action
    public boolean pressKey(By by,String action) throws Exception
    {
        boolean ret = false;

        try
        {
            ret=waitForElement().until(ExpectedConditions.visibilityOfElementLocated(by)).isDisplayed();

            if(action.equals("ENTER"))
                driver.findElement(by).sendKeys(Keys.ENTER);

            if(action.equals("ESC"))
                driver.findElement(by).sendKeys(Keys.ESCAPE);

            if(action.equals("ARROW_DOWN"))
                driver.findElement(by).sendKeys(Keys.ARROW_DOWN);
            Thread.sleep(2000);

        }

        catch(NoSuchElementException e)
        {
            Assert.assertEquals(ret,"No such element found in Page");
            e.printStackTrace();
        }
        return ret;
    }


    public void refresh() throws Exception
    {
        try
        {
            driver.navigate().refresh();
        }

        catch(Exception e)
        {
            e.printStackTrace();
        }

    }




    public boolean enterValueInToRickTextEditor(By by,String textValue) throws Exception
    {
        boolean ret = false;
        String text="arguments[0].innerHTML = '<p>"+textValue+"</p>'";
        try
        {
            ret=waitForElement().until(ExpectedConditions.visibilityOfElementLocated(by)).isDisplayed();
            WebElement element = driver.findElement(by);
            element.clear();
            JavascriptExecutor js=(JavascriptExecutor)driver;
            js.executeScript(text, element);

        }

        catch(NoSuchElementException e)
        {
            Assert.assertEquals(ret,"No such element found in Page");
            e.printStackTrace();
        }
        return ret;
    }

    //excute a javascript to get the value of the javascript verible
    public String getJSVeribleValue(String id){
        String script = "return document.getElementById('"+id+"').value;";
        JavascriptExecutor js = (JavascriptExecutor)driver;
        Object val=js.executeScript(script);
        return val.toString();


    }



    public boolean selectOptionElementsByLocator(By by, String elementText)  throws Exception

    {
        boolean ret = false;

        try
        {
            ret=waitForElement().until(ExpectedConditions.visibilityOfElementLocated(by)).isDisplayed();
            if(ret)
            {
                ret=true;
                select=new Select(driver.findElement(by));
                select.selectByVisibleText(elementText);
            }
        }

        catch(NoSuchElementException e)
        {
            ret=false;
            e.printStackTrace();
        }
        return ret;
    }



    //find the index of the element you want to select
    public int FindOptionIndexByLocator(By by, String elementText)  throws Exception

    {
        boolean ret = false;
        int index=0;
        try
        {
            ret=waitForElement().until(ExpectedConditions.visibilityOfElementLocated(by)).isDisplayed();
            if(ret)
            {
                ret=true;
                elements=getAllElementsByLocator(by);

                for(int i=0; i<elements.size(); i++){
                    if(elements.get(i).getText().equalsIgnoreCase(elementText)){
                        index=i;
                    }

                }

            }
        }

        catch(NoSuchElementException e)
        {
            e.printStackTrace();
        }
        return index;

    }






    public WebDriver switchToframeByIndex(int index) throws Exception
    {

        try
        {
            //waitForElement().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(Integer.toString(index)));
            Thread.sleep(2000);
            driver.switchTo().frame(index)	;
        }

        catch(NoSuchFrameException e)
        {
            e.printStackTrace();
        }
        return driver;

    }


    public WebDriver switchToframe(String frame) throws Exception
    {

        try
        {

            Thread.sleep(2000);
            driver.switchTo().frame(frame);
        }

        catch(NoSuchFrameException e)
        {
            e.printStackTrace();
        }
        return driver;

    }


    public WebDriver switchToDefaultFrame() throws Exception
    {

        try
        {
            driver.switchTo().defaultContent();
        }

        catch(NoSuchFrameException e)
        {
            e.printStackTrace();
        }
        return driver;

    }



    public void applyImplicitWait(int duration) throws Exception
    {
        try
        {
            driver.manage().timeouts().implicitlyWait(duration, TimeUnit.SECONDS);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
    public WebDriver switchToMainWindow() throws Exception
    {
        return driver.switchTo().defaultContent();
    }

    public boolean verifyIfElementExist(By by,int timeMiliSeconds) throws Exception
    {
        int TimeMiliSeconds=timeMiliSeconds;

        boolean iret=false;
        try
        {
            for(int second=0;;second++)
            {
                if(second>=TimeMiliSeconds)
                {
                    System.out.println("element no found");
                    break;

                }
                if(driver.findElements(by).size()>0)
                {

                    iret=true;
                    break;
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return iret;
    }


    public boolean isElementPresent(By locator) {
        try {
            if( driver.findElement(locator).isEnabled());
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }




    public void clickElement(String element){
        boolean clicked=true;

        try {
            driver.findElement(By.id(element)).click();
        } catch (NoSuchElementException e) {
            clicked = false;
        }
        if(!clicked){
            try {
                driver.findElement(By.className(element)).click();
            } catch (NoSuchElementException e) {}
        }
    }





    public String generate_random(int length){
        return RandomStringUtils.randomAlphanumeric(length).toUpperCase();


    }



    public boolean isElementEnabled(By by){

        boolean isEnabled=false;
        try
        {
            if(driver.findElement(by).isEnabled())
                isEnabled= true;


        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return isEnabled;

    }




    public void nevigateTo(String link){
        driver.navigate().to(link);
    }


    // if the scenario is failed take a screenshot
    public void takeScreenShot(Scenario scenario){
        try {
            if (scenario.isFailed()) {
                final byte[] screenshot = ((TakesScreenshot) driver)
                        .getScreenshotAs(OutputType.BYTES);
                scenario.embed(screenshot, "image/png");

            }
        } catch(Exception e){

            e.printStackTrace();
        }
    }



}
