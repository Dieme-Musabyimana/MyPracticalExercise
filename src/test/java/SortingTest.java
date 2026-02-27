import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.Test;
import java.util.*;

public class SortingTest {


    @Test
    void validateAllSortingOptions() {
        try (Playwright playwright = Playwright.create()) {
            boolean isCI = System.getenv("CI") != null;

            // 1. Launch a clean browser (no persistence)
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(isCI)
            );

            // 2. Add a Human User Agent to avoid being blocked by Cloudflare
            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
            );

            Page page = context.newPage();
            page.setDefaultTimeout(60000);

            // 3. Navigate and wait for the page to load
            page.navigate("https://practicesoftwaretesting.com/");

            // 4. Wait for the initial product list to appear
            Locator products = page.locator("[data-test='product-name']");
            products.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

            // Core Sorting Logic
            List<String> sortValues = Arrays.asList("name,asc", "name,desc", "price,desc", "price,asc");

            for (String value : sortValues) {
                // Select the option from the dropdown
                page.locator("select[data-test='sort']").selectOption(value);

                // 5. Wait for the network to be quiet after sorting
                // This is more reliable than waiting for a specific 'completed' tag
                page.waitForLoadState(LoadState.NETWORKIDLE);

                // Re-verify the product list is visible after the sort
                products.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

                String firstProduct = products.first().innerText();
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
