
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.Test;
import java.util.*;

public class SortingTest {

    @Test
    void validateAllSortingOptions() {
        try (Playwright playwright = Playwright.create()) {
            boolean isCI = System.getenv("CI") != null;

            // standard launch without persistence or bot-evasion arguments
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(isCI)
            );

            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            page.setDefaultTimeout(60000);

            page.navigate("https://practicesoftwaretesting.com/");

            // Essential: Wait for products to appear in the DOM
            page.locator("[data-test='product-name']").first().waitFor();

            // Core Sorting Logic
            List<String> sortValues = Arrays.asList("name,asc", "name,desc", "price,desc", "price,asc");

            for (String value : sortValues) {
                // Wait for dropdown to be ready and select option
                Locator sortDropdown = page.locator("select[data-test='sort']");
                sortDropdown.waitFor();
                sortDropdown.selectOption(value);

                // Wait for the UI to acknowledge the sort is done
                page.waitForSelector("[data-test='sorting_completed']");

                // Verify result
                String firstProduct = page.locator("[data-test='product-name']").first().innerText();
                System.out.println("Validated Sort [" + value + "] - Top Item: " + firstProduct.trim());
            }

            browser.close();
        }
    }
}

//
//import com.microsoft.playwright.*;
//import com.microsoft.playwright.options.*;
//import org.junit.jupiter.api.Test;
//import java.nio.file.Paths;
//import java.util.*;
//
//public class SortingTest {
////Start test method
//    @Test
//    void validateAllSortingOptions() {
//        try (Playwright playwright = Playwright.create()) {
//            boolean isCI = System.getenv("CI") != null;
//
//            // Keep these: They ensure the GitHub Runner isn't blocked as a bot
//            BrowserType.LaunchPersistentContextOptions options = new BrowserType.LaunchPersistentContextOptions()
//                    .setHeadless(isCI)
//                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
//                    .setArgs(Arrays.asList("--no-sandbox", "--disable-blink-features=AutomationControlled"));
//
//            BrowserContext context = playwright.chromium().launchPersistentContext(Paths.get("target/profiles"), options);
//            Page page = context.pages().get(0);
//            page.setDefaultTimeout(60000);
//
//            page.navigate("https://practicesoftwaretesting.com/", new Page.NavigateOptions().setWaitUntil(WaitUntilState.LOAD));
//
//            // Essential: Wait for SPA to load product data
//            boolean loaded = false;
//            for (int i = 0; i < 30; i++) {
//                if (page.locator("[data-test='product-name']").count() > 0) {
//                    loaded = true;
//                    break;
//                }
//                page.waitForTimeout(1000);
//            }
//
//            if (!loaded) {
//                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("target/error.png")));
//                throw new RuntimeException("Products did not load in time.");
//            }
//
//            // Core Sorting Logic
//            List<String> sortValues = Arrays.asList("name,asc", "name,desc", "price,desc", "price,asc");
//            for (String value : sortValues) {
//                page.locator("select[data-test='sort']").selectOption(value);
//
//                // Wait for the UI to acknowledge the sort is done
//                page.waitForSelector("[data-test='sorting_completed']");
//
//                String firstProduct = page.locator("[data-test='product-name']").first().innerText();
//                System.out.println("Validated Sort [" + value + "] - Top Item: " + firstProduct.trim());
//            }
//
//            context.close();
//        }
//    }
//}
//
