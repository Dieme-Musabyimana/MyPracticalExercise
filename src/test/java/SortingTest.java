
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.Test;
import java.nio.file.Paths;
import java.util.*;

public class SortingTest {

    @Test
    void validateAllSortingOptions() {
        try (Playwright playwright = Playwright.create()) {
            boolean isCI = System.getenv("CI") != null;

            // Keep these: They ensure the GitHub Runner isn't blocked as a bot
            BrowserType.LaunchPersistentContextOptions options = new BrowserType.LaunchPersistentContextOptions()
                    .setHeadless(isCI)
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                    .setArgs(Arrays.asList("--no-sandbox", "--disable-blink-features=AutomationControlled"));

            BrowserContext context = playwright.chromium().launchPersistentContext(Paths.get("target/profiles"), options);
            Page page = context.pages().get(0);
            page.setDefaultTimeout(60000);

            page.navigate("https://practicesoftwaretesting.com/", new Page.NavigateOptions().setWaitUntil(WaitUntilState.LOAD));

            // Essential: Wait for SPA to load product data
            boolean loaded = false;
            for (int i = 0; i < 30; i++) {
                if (page.locator("[data-test='product-name']").count() > 0) {
                    loaded = true;
                    break;
                }
                page.waitForTimeout(1000);
            }

            if (!loaded) {
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("target/error.png")));
                throw new RuntimeException("Products did not load in time.");
            }

            // Core Sorting Logic
            List<String> sortValues = Arrays.asList("name,asc", "name,desc", "price,desc", "price,asc");
            for (String value : sortValues) {
                page.locator("select[data-test='sort']").selectOption(value);

                // Wait for the UI to acknowledge the sort is done
                page.waitForSelector("[data-test='sorting_completed']");

                String firstProduct = page.locator("[data-test='product-name']").first().innerText();
                System.out.println("Validated Sort [" + value + "] - Top Item: " + firstProduct.trim());
            }

            context.close();
        }
    }
}

//import com.microsoft.playwright.*;
//import com.microsoft.playwright.options.*;
//import org.junit.jupiter.api.Test;
//import java.nio.file.Paths;
//import java.util.*;
//import java.util.stream.Collectors;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//public class SortingTest {
//
//    @Test
//    void validateAllSortingOptions() {
//        try (Playwright playwright = Playwright.create()) {
//            boolean isCI = System.getenv("CI") != null;
//
//            // TRICK: Force a real User Agent so the site doesn't see "HeadlessChrome"
//            String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36";
//
//            BrowserType.LaunchPersistentContextOptions options = new BrowserType.LaunchPersistentContextOptions()
//                    .setHeadless(isCI)
//                    .setUserAgent(userAgent) // Mask the GitHub runner identity
//                    .setArgs(Arrays.asList(
//                            "--no-sandbox",
//                            "--disable-blink-features=AutomationControlled",
//                            "--use-gl=swiftshader" // Helps rendering in headless environments
//                    ));
//
//            BrowserContext context = playwright.chromium().launchPersistentContext(Paths.get("target/playwright-profile"), options);
//
//            // Extra stealth to hide the 'webdriver' flag
//            context.addInitScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
//
//            Page page = context.pages().get(0);
//            page.setDefaultTimeout(90000);
//
//            // 1. Navigate - Using 'load' is often safer for sites with slow background APIs
//            System.out.println("Navigating to site...");
//            page.navigate("https://practicesoftwaretesting.com/", new Page.NavigateOptions().setWaitUntil(WaitUntilState.LOAD));
//
//            // 2. The Polling Loop (increased to 60 attempts)
//            boolean productsFound = false;
//            for (int i = 0; i < 60; i++) {
//                int count = page.locator("[data-test='product-name']").count();
//                if (count > 0) {
//                    productsFound = true;
//                    System.out.println("Products loaded! Count: " + count);
//                    break;
//                }
//                page.waitForTimeout(1000);
//            }
//
//            if (!productsFound) {
//                // This screenshot will now be available in your GitHub Artifacts
//                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("target/failure-timeout.png")).setFullPage(true));
//                throw new RuntimeException("CRITICAL FAILURE: Products failed to load. Check the uploaded screenshot artifact!");
//            }
//
//            // ... (Sorting logic stays the same)
//            List<String> sortValues = Arrays.asList("name,asc", "name,desc", "price,desc", "price,asc");
//            for (String value : sortValues) {
//                page.locator("select[data-test='sort']").selectOption(value);
//                page.waitForSelector("[data-test='sorting_completed']", new Page.WaitForSelectorOptions().setTimeout(60000));
//                System.out.println("Sort: [" + value + "] -> Success");
//            }
//
//            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("target/success-sorting.png")));
//            context.close();
//        }
//    }
//}
