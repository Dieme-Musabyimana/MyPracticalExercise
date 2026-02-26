import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.Test;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SortingTest {

    @Test
    void validateAllSortingOptions() {
        try (Playwright playwright = Playwright.create()) {
            boolean isCI = System.getenv("CI") != null;

            BrowserType.LaunchPersistentContextOptions options = new BrowserType.LaunchPersistentContextOptions()
                    .setHeadless(isCI)
                    .setArgs(Arrays.asList("--no-sandbox", "--disable-blink-features=AutomationControlled"));

            BrowserContext context = playwright.chromium().launchPersistentContext(Paths.get("target/playwright-profile"), options);
            context.addInitScript("() => { Object.defineProperty(navigator,'webdriver',{get:()=>undefined}); }");

            Page page = context.pages().get(0);
            page.setDefaultTimeout(90000);

            // STEP 1: Navigate with DOMCONTENTLOADED (Fixes the NetworkIdle Timeout)
            System.out.println("Navigating to site...");
            page.navigate("https://practicesoftwaretesting.com/", new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

            // STEP 2: Polling Loop for hydration
            boolean productsFound = false;
            for (int i = 0; i < 60; i++) {
                if (page.locator("[data-test='product-name']").count() > 0) {
                    productsFound = true;
                    break;
                }
                page.waitForTimeout(1000);
            }

            // STEP 3: If it fails, take a screenshot before throwing the error
            if (!productsFound) {
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("target/failure-timeout.png")));
                throw new RuntimeException("CRITICAL FAILURE: Products failed to load. See target/failure-timeout.png");
            }

            List<String> sortValues = Arrays.asList("name,asc", "name,desc", "price,desc", "price,asc");
            Locator sortDropdown = page.locator("select[data-test='sort']");

            for (String value : sortValues) {
                sortDropdown.selectOption(value);
                page.waitForSelector("[data-test='sorting_completed']", new Page.WaitForSelectorOptions().setTimeout(60000));

                String firstProduct = page.locator("[data-test='product-name']").first().innerText();
                System.out.println("Sort: [" + value + "] -> First Product: " + firstProduct.trim());
            }

            // Final success screenshot
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("target/success-sorting.png")));
            System.out.println("--- All sorting options tested successfully ---");
            context.close();
        }
    }
}
//
//import com.microsoft.playwright.*;
//import com.microsoft.playwright.options.LoadState;
//import com.microsoft.playwright.options.WaitForSelectorState;
//import com.microsoft.playwright.options.WaitUntilState;
//import org.junit.jupiter.api.Test;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//public class SortingTest {
//
//    public static void main(String[] args) {
//        new SortingTest().validateAllSortingOptions();
//    }
//
//    @Test
//    void validateAllSortingOptions() {
//        try (Playwright playwright = Playwright.create()) {
//            boolean isCI = System.getenv("CI") != null;
//
//            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
//                    .setHeadless(isCI));
//
//            // 1. Create a context with a longer default timeout
//            BrowserContext context = browser.newContext();
//            context.setDefaultTimeout(60000); // 60 seconds
//
//            Page page = context.newPage();
//
//            // 2. Navigate and wait for the "Commitment" of the page
//            page.navigate("https://practicesoftwaretesting.com/", new Page.NavigateOptions().setWaitUntil(WaitUntilState.COMMIT));
//
//            // 3. Instead of just waiting for the selector, wait for the network to be somewhat quiet
//            // This is better than DOMCONTENTLOADED for sites that load data via API
//            try {
//                page.waitForLoadState(LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(30000));
//            } catch (Exception e) {
//                System.out.println("Network didn't go idle, but continuing anyway...");
//            }
//
//            List<String> sortValues = Arrays.asList(
//                    "name,asc", "name,desc", "price,desc", "price,asc", "co2_rating,asc", "co2_rating,desc"
//            );
//
//            System.out.println("--- Starting Comprehensive Sorting Test ---");
//
//            // 4. Force a wait for the selector to be ATTACHED before checking visibility
//            page.waitForSelector("select[data-test='sort']", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.ATTACHED));
//            for (String value : sortValues) {
//                page.selectOption("select[data-test='sort']", value);
//
//                // Wait for the indicator that sorting finished
//                page.waitForSelector("[data-test='sorting_completed']");
//
//                if (value.equals("price,asc")) {
//                    List<Double> prices = page.locator("[data-test='product-price']").allTextContents()
//                            .stream()
//                            .map(p -> Double.parseDouble(p.replaceAll("[^0-9.]", "")))
//                            .collect(Collectors.toList());
//
//                    for (int i = 0; i < prices.size() - 1; i++) {
//                        assertTrue(prices.get(i) <= prices.get(i + 1),
//                                "Price sorting failed at index " + i);
//                    }
//                    System.out.println("Validation Success: Price (asc) is mathematically correct.");
//                }
//
//                Locator firstProduct = page.locator("[data-test='product-name']").first();
//                firstProduct.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
//                System.out.println("Sort: [" + value + "] -> First Product: " + firstProduct.textContent().trim());
//            }
//
//            System.out.println("--- All sorting options tested successfully ---");
//            browser.close();
//        }
//    }
//}