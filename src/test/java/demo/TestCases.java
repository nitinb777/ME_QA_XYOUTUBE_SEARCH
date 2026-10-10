package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;

import demo.utils.ExcelDataProvider;
// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;

public class TestCases extends ExcelDataProvider { // Lets us read the data
        ChromeDriver driver;

        @BeforeTest
        public void startBrowser() {
                System.setProperty("java.util.logging.config.file", "logging.properties");

                ChromeOptions options = new ChromeOptions();
                LoggingPreferences logs = new LoggingPreferences();

                logs.enable(LogType.BROWSER, Level.ALL);
                logs.enable(LogType.DRIVER, Level.ALL);
                options.setCapability("goog:loggingPrefs", logs);
                options.addArguments("--remote-allow-origins=*");

                System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");

                driver = new ChromeDriver(options);

                driver.manage().window().maximize();
        }

        @Test
        public void testCase01() {
                driver.get("https://www.youtube.com/");

                String currentUrl = driver.getCurrentUrl();

                Assert.assertTrue(
                                currentUrl.contains("youtube.com"),
                                "YouTube URL is not correct");
                WebElement about = driver.findElement(
                                By.xpath("//a[@href='https://www.youtube.com/about/']"));

                driver.executeScript("arguments[0].click();", about);

                WebElement message = driver.findElement(
                                By.xpath("//p[@data-cy='pageSubHeading']"));

                System.out.println(message.getText());
        }

        @Test
        public void testCase02() {

                driver.get("https://www.youtube.com/");

                // Wait for Movies & TV
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

                WebElement movies = wait.until(
                                ExpectedConditions.presenceOfElementLocated(
                                                By.xpath("//yt-formatted-string[normalize-space()='Movies & TV']")));

                driver.executeScript(
                                "arguments[0].scrollIntoView({block:'center'});",
                                movies);

                driver.executeScript(
                                "arguments[0].click();",
                                movies);

                wait.until(
                                ExpectedConditions.presenceOfElementLocated(
                                                By.xpath("//span[@id='title' and normalize-space()='Top selling']")));

                List<WebElement> movieList = driver.findElements(
                                By.xpath(
                                                "//span[@id='title' and normalize-space()='Top selling']" +
                                                                "/ancestor::ytd-shelf-renderer" +
                                                                "//ytd-grid-movie-renderer"));

                System.out.println("Total movies: " + movieList.size());

                WebElement rightmostMovie = movieList.get(0);

                for (int i = 1; i < movieList.size(); i++) {

                        if (movieList.get(i).getLocation().getX() > rightmostMovie.getLocation().getX()) {

                                rightmostMovie = movieList.get(i);
                        }
                }

                WebElement title = rightmostMovie.findElement(
                                By.xpath(".//span[@id='video-title']"));

                String movieTitle = title.getAttribute("title");

                if (movieTitle == null || movieTitle.isEmpty()) {
                        movieTitle = title.getText();
                }

                System.out.println("Rightmost movie: " + movieTitle);

                List<WebElement> matureBadge = rightmostMovie.findElements(
                                By.xpath(".//badge-shape[@aria-label='A']"));

                boolean isMature = matureBadge.size() > 0;

                System.out.println("Is movie A rated: " + isMature);

                WebElement category = rightmostMovie.findElement(
                                By.xpath(".//span[contains(@class,'grid-movie-renderer-metadata')]"));

                String categoryText = category.getText();

                System.out.println("Movie category: " + categoryText);

                boolean validCategory = categoryText.contains("Comedy") ||
                                categoryText.contains("Animation") ||
                                categoryText.contains("Drama");

                if (validCategory) {
                        System.out.println("Category is valid");
                } else {
                        System.out.println("Category is NOT valid");
                }
        }

        @Test
        public void testCase03() {

                driver.get("https://www.youtube.com/channel/UC-9-kyTW8ZkZNDHQJ6FgpwQ");

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

                // Find the first section heading
                WebElement heading = wait.until(
                                ExpectedConditions.visibilityOfElementLocated(
                                                By.xpath("//span[@id='title' and normalize-space()=\"India's Biggest Hits\"]")));

                System.out.println("First section: " + heading.getText());

                // Find all playlist cards inside this section
                List<WebElement> playlists = driver.findElements(
                                By.xpath(
                                                "//span[@id='title' and normalize-space()=\"India's Biggest Hits\"]" +
                                                                "/ancestor::ytd-rich-shelf-renderer" +
                                                                "//ytd-rich-item-renderer"));

                System.out.println("Total playlists: " + playlists.size());

                // Print each playlist name

                WebElement rightmostPlaylist = playlists.get(0);

                for (int i = 1; i < playlists.size(); i++) {
                        int currentX = playlists.get(i).getLocation().getX();
                        int rightmostX = rightmostPlaylist.getLocation().getX();

                        if (currentX > rightmostX) {
                                rightmostPlaylist = playlists.get(i);
                        }
                }

                String name = rightmostPlaylist.findElement(
                                By.xpath(".//h3[@title]")).getAttribute("title");

                String songs = rightmostPlaylist.findElement(
                                By.xpath(".//div[contains(@class,'ytBadgeShapeText')]")).getText();
                System.out.println("Rightmost playlist: " + name);
                System.out.println("Track count: " + songs);

                int trackCount = Integer.parseInt(songs.split(" ")[0]);

                if (trackCount <= 50) {
                        System.out.println("Track count is 50 or fewer: true");
                } else {
                        System.out.println("Track count is 50 or fewer: false");
                }
        }

        @Test
        public void testCase04() {

                driver.get("https://www.youtube.com");

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

                WebElement showMore = wait.until(
                                ExpectedConditions.elementToBeClickable(
                                                By.xpath("//ytd-guide-entry-renderer[.//yt-formatted-string[normalize-space()='Show more']]")));

                showMore.click();

                List<WebElement> newsLinks = wait.until(
                                ExpectedConditions.presenceOfAllElementsLocatedBy(
                                                By.xpath("//a[@id='endpoint' and @title='News']")));

                System.out.println("News links found after Show more: " + newsLinks.size());

                WebElement newsLink = wait.until(
                                ExpectedConditions.elementToBeClickable(
                                                By.xpath("//a[@id='endpoint' and @title='News']")));

                newsLink.click();

                wait.until(ExpectedConditions.urlContains("/channel/UCYfdidRxbB8Qhf0Nx7ioOYw"));

                System.out.println("News page opened: " + driver.getCurrentUrl());

                WebElement newsSection = wait.until(
                                ExpectedConditions.presenceOfElementLocated(
                                                By.xpath("//ytd-rich-shelf-renderer[.//span[@id='title' and contains(translate(normalize-space(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'latest news posts')]]")));

                List<WebElement> posts = newsSection.findElements(
                                By.cssSelector("ytd-rich-item-renderer"));

                System.out.println("Posts found: " + posts.size());

                int totalLikes = 0;

                for (int i = 0; i < Math.min(3, posts.size()); i++) {
                        WebElement post = posts.get(i);

                        WebElement bodyElement = post.findElement(
                                        By.cssSelector("yt-formatted-string#home-content-text"));

                        String body = bodyElement.getText().trim();

                        List<WebElement> likeElements = post.findElements(
                                        By.cssSelector("span#vote-count-middle"));

                        int likes = 0;

                        if (!likeElements.isEmpty()) {
                                String likeText = likeElements.get(0).getText().trim();

                                if (!likeText.isEmpty()) {
                                        likes = Integer.parseInt(likeText.replace(",", ""));
                                }
                        }

                        totalLikes += likes;

                        System.out.println("\nPost " + (i + 1));
                        System.out.println("Body: " + body);
                        System.out.println("Likes: " + likes);
                }

                System.out.println("\nTotal likes for first 3 posts: " + totalLikes);
        }

        @Test(dataProvider = "excelData")
        public void testCase05(String searchTerm) {
                driver.get("https://www.youtube.com/");

                WebDriverWait wait = new WebDriverWait(
                                driver, Duration.ofSeconds(15));

                WebElement searchBox = wait.until(
                                ExpectedConditions.visibilityOfElementLocated(
                                                By.name("search_query")));

                searchTerm = searchTerm.trim();

                Wrappers.sendKeys(driver, searchBox, searchTerm);
                Wrappers.pressEnter(driver, searchBox);

                wait.until(
                                ExpectedConditions.presenceOfElementLocated(
                                                By.cssSelector("ytd-video-renderer")));

                long totalViews = 0;
                long targetViews = 100_000_000;
                int videoCount = 0;
                int previousVideoCount = 0;

                java.util.Set<String> countedVideos = new java.util.HashSet<>();

                System.out.println("\nSearch term: " + searchTerm);

                while (totalViews < targetViews) {

                        List<WebElement> videos = driver.findElements(
                                        By.cssSelector("ytd-video-renderer"));

                        for (WebElement video : videos) {
                                try {
                                        WebElement titleElement = video.findElement(
                                                        By.cssSelector("#video-title"));

                                        String title = titleElement.getText();
                                        String url = titleElement.getAttribute("href");

                                        if (url == null || !countedVideos.add(url)) {
                                                continue;
                                        }

                                        String viewsText = video.findElement(
                                                        By.cssSelector("span.inline-metadata-item"))
                                                        .getText().trim();

                                        // Skip live videos showing current viewers.
                                        if (viewsText.toLowerCase().contains("watching")) {
                                                continue;
                                        }

                                        long views = convertViewsToNumber(viewsText);

                                        totalViews += views;
                                        videoCount++;

                                        System.out.println("Title: " + title);
                                        System.out.println("Views: " + viewsText);
                                        System.out.println("Running total: " + totalViews);

                                        if (totalViews >= targetViews) {
                                                break;
                                        }

                                } catch (Exception e) {
                                        System.out.println(
                                                        "Could not read a video's details.");
                                }
                        }

                        if (totalViews >= targetViews) {
                                break;
                        }

                        if (videos.size() == previousVideoCount) {
                                System.out.println(
                                                "No additional videos loaded. Stopping.");
                                break;
                        }

                        previousVideoCount = videos.size();
                        int expectedVideoCount = previousVideoCount;

                        Wrappers.scrollToBottom(driver);

                        try {
                                new WebDriverWait(driver, Duration.ofSeconds(5))
                                                .until(d -> d.findElements(
                                                                By.cssSelector("ytd-video-renderer"))
                                                                .size() > expectedVideoCount);

                        } catch (org.openqa.selenium.TimeoutException e) {
                                System.out.println(
                                                "More videos did not load within 5 seconds.");
                                break;
                        }
                }

                System.out.println("Videos counted: " + videoCount);
                System.out.println("Final total views: " + totalViews);

                if (totalViews >= targetViews) {
                        System.out.println(
                                        "Target of 10 crore views reached.");
                } else {
                        System.out.println(
                                        "Target not reached with the loaded results.");
                }
        }

        private long convertViewsToNumber(String viewsText) {
                viewsText = viewsText.trim()
                                .replace(",", "")
                                .replace(" views", "");

                if (viewsText.endsWith("K")) {
                        return (long) (Double.parseDouble(
                                        viewsText.replace("K", "")) * 1_000);
                } else if (viewsText.endsWith("M")) {
                        return (long) (Double.parseDouble(
                                        viewsText.replace("M", "")) * 1_000_000);
                } else if (viewsText.endsWith("B")) {
                        return (long) (Double.parseDouble(
                                        viewsText.replace("B", "")) * 1_000_000_000);
                }

                return Long.parseLong(viewsText);
        }

        @AfterTest
        public void endTest() {
                driver.close();
                driver.quit();

        }
}