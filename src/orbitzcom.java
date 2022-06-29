import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public class orbitzcom {

    public static void main(String[] args) throws InterruptedException {

        System.setProperty("webdriver.chrome.driver", "/Users/mervegurleyen/Desktop/BrowserDrivers/chromedriver-3");
        WebDriver driver = new ChromeDriver();

        //Navigate to orbitz.com
        driver.get("https://www.orbitz.com/");

        //Enter Orlando for Going to field.
        driver.findElement(By.xpath("//button[@aria-label='Going to']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("location-field-destination")));
        driver.findElement(By.id("location-field-destination")).sendKeys("Orlando", Keys.ENTER);
        driver.findElement(By.id("d1-btn")).click();
        Thread.sleep(300);

        Thread.sleep(4000);
//      Choose July 20 as Check In and July 24 as Check Out dates
        driver.findElement(By.xpath("(//table[@class='uitk-date-picker-weeks'])[1]//tr//td//button[@data-day='20']")).click();

        driver.findElement(By.xpath("(//table[@class='uitk-date-picker-weeks'])[1]//tr//td//button[@data-day='24']")).click();
        driver.findElement(By.xpath("//button[@data-stid='apply-date-picker']")).click();

//        In the Travelers menu choose the following
        driver.findElement(By.xpath("//button[@aria-label='1 room, 2 travelers']")).click();
        Thread.sleep(4000);
        driver.findElement(By.xpath("(//button[@class='uitk-layout-flex-item uitk-step-input-touch-target'])[1]")).click();
        driver.findElement(By.xpath("(//button[@class='uitk-layout-flex-item uitk-step-input-touch-target'])[4]")).click();
        Select child1 = new Select(driver.findElement(By.id("child-age-input-0-0")));

        child1.selectByIndex(5);
        driver.findElement(By.xpath("(//button[@class='uitk-layout-flex-item uitk-step-input-touch-target'])[4]")).click();
        Thread.sleep(4000);

        Select child2 = new Select(driver.findElement(By.id("child-age-input-0-1")));
        child2.selectByIndex(9);
        driver.findElement(By.xpath("//button[@data-testid='guests-done-button']")).click();
        driver.findElement(By.xpath("//button[@data-testid='submit-button']")).click();
        Thread.sleep(2000);

//        Click search. If you encounter a message to check if you are a bot or a human, change your WebDriver initialization in the following way:

        WebElement breakfast = driver.findElement(By.xpath("//input[@id='popularFilter-0-FREE_BREAKFAST']"));
//    //        Click on the x on the Breakfast included button and verify that the checkbox is now unchecked.
        if (!breakfast.isSelected()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", breakfast);
        }

        String actual = driver.getPageSource();
        Thread.sleep(4000);
        Assert.assertTrue(actual.contains("Breakfast included"));

        if (breakfast.isSelected()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", breakfast);

        }
        Thread.sleep(4000);

//        Set the Your Budget value to $270 and verify that Less than $270 button appears on top of the results
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,500)", "");

        WebElement budget = driver.findElement(By.xpath("//input[@id='price-slider-secondary']"));
        budget.sendKeys(Keys.ARROW_LEFT);
        Thread.sleep(4000);
        String actualPrice = driver.findElement(By.xpath("//button[@id='playback-filter-pill-price-0.0-270.0']")).getText();
        Thread.sleep(4000);
        Assert.assertEquals(actualPrice, "Less than $270");

//        Also grab all the prices from the search results, verify that there are 50 results, and each price is less than or equal to 270.
//(You might need to scroll the window down for all the results to appear)

        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,3000)", "");
        List<WebElement> hotelList = driver.findElements(By.xpath("//li[contains(@class, 'uitk-spacing-margin-blockstart-three')][@tabindex=\"-1\"]"));
        Thread.sleep(4000);
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,3000)", "");
        Assert.assertEquals(hotelList.size(), 50);
        Thread.sleep(4000);

        List<WebElement> hotelprice = driver.findElements(By.xpath("//div[contains(text(), \"The price is\")]"));

        for (WebElement price : hotelprice) {
            Assert.assertTrue((Integer.parseInt(price.getText().replaceAll("[$,  The price is]", "")) <= 270));
        }


//         Click on Wonderful 4.5+ radio button under Guest Rating
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(By.id("radio-guestRating-45")));
        Thread.sleep(4000);

//        Grab all the ratings from the search results, and verify that each rating is greater than or equal to 4.5.
        List<WebElement> rating = driver.findElements(By.xpath("//span[@class='uitk-text uitk-type-300 uitk-type-bold uitk-text-default-theme']"));

        for (WebElement hotelRatings : rating) {
            Assert.assertTrue((Double.parseDouble(hotelRatings.getText().substring(0, 3)) >= 4.5));
        }

//        From the search results click on the last (50th) result. The hotel name could vary in your case. Make sure to
//        store hotel’s name and rating into a variable before clicking.

        hotelList = driver.findElements(By.xpath("//li[contains(@class, 'uitk-spacing-margin-blockstart-three')][@tabindex=\"-1\"]"));

        WebElement lastHotel = hotelList.get(hotelList.size() - 1);
        String lastHotelName = lastHotel.findElement(By.xpath(".//child::*[1]")).getText();
        double lastHotelRating = Double.valueOf(lastHotel.findElement(By.xpath(".//span[contains(text(), 'out of 5')]//preceding-sibling::span")).getText().split("/")[0]);
        lastHotel.click();

//        The information about that specific hotel will open in a new tab. Verify that you are in a new tab by
//        verifying the title. Verify that the name and the rating match the previous page’s values.

        String mainwindowHandle = driver.getWindowHandle();
        Set<String> windowHandles = driver.getWindowHandles();
        Thread.sleep(6000);
        for (String title : windowHandles) {
            if (!title.equals(mainwindowHandle)) {
                driver.switchTo().window(title);
            }
        }

        Assert.assertEquals(lastHotelName, driver.getTitle());

        String currentWindowHotelName = driver.findElement(By.xpath("//div[@data-stid='content-hotel-title']//h1")).getText();
        double currentWindowHotelRating = Double.valueOf(driver.findElement(By.xpath("//div[@data-stid='content-hotel-reviewsummary']//h3")).getText().split("/")[0]);

        Assert.assertEquals(currentWindowHotelName, lastHotelName);
        Assert.assertEquals(currentWindowHotelRating, lastHotelRating);

//        Close the current active tab.

        driver.close();

//        Once you are in the first tab again, click on Orbitz logo that takes you to the homepage
        Thread.sleep(6000);
        driver.switchTo().window(mainwindowHandle);

        driver.findElement(By.xpath("//a[@href='/']")).click();
        Thread.sleep(15000);

        WebElement iframe = driver.findElement(By.xpath("//iframe[contains(@id,'vac_iframe_')]"));
        driver.switchTo().frame(iframe);

//        Once on the homepage, click on the help button on the right bottom corner
        driver.findElement(By.xpath("//button[@aria-label='Get help from your Virtual Agent.']")).click();

        Thread.sleep(3000);

//         Verify that the virtual agent is active and the indicated text is displayed
        String actualSource = driver.findElement(By.xpath("(//div[@data-test-id='chat-text-message'])[1]")).getText();
        Thread.sleep(6000);
        Assert.assertTrue(actualSource.contains("Hi, I'm your Virtual Agent "));
        Thread.sleep(8000);

//        Close the virtual agent by clicking on minus(-) button

        driver.findElement(By.xpath("//button[@id='vac-close-button']")).click();
        Thread.sleep(3000);

//        On the main page do a final verification by clicking on More Travel and verify the texts of all the links

        driver.switchTo().defaultContent();
        driver.findElement(By.xpath("//div[@id='gc-custom-header-tool-bar-shop-menu']")).click();
        Thread.sleep(750);
        String[] actualOptions = {"Stays", "Flights", "Packages", "Cars", "Cruises", "Things to do", "Deals", "Groups & meetings", "Travel Blog" };
        List<WebElement> moreOptions = driver.findElements(By.xpath("(//div[@class='uitk-list'])[1]//a[@role='link']"));

        for (int i = 0; i < moreOptions.size(); i++) {
            Assert.assertEquals(moreOptions.get(i).getText(), actualOptions[i]);
        }

//        Quit the Wedriver session.
        Thread.sleep(750);
        driver.quit();
    }
}
