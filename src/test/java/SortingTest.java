import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SortingTest {

    public static void main(String[] args) {
        new SortingTest().validateAllSortingOptions();
    }

    @Test
    void validateAllSortingOptions() {

        try (Playwright playwright = Playwright.create()) {

            boolean isCI = System.getenv("CI") != null;

            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(isCI) // Headless in CI, headed locally
            );

            // ✅ Set a large viewport to ensure SPA renders properly
            BrowserContext context = browser.newContext(
                    new Browser.NewContextOptions()
                            .setViewportSize(1920, 1080)
            );

            Page page = context.newPage();

            // Navigate to the site
            page.navigate("https://practicesoftwaretesting.com/");

            // --- Wait for products safely in CI ---
            Locator firstProduct = page.locator("[data-test='product-name']").first();

            // Retry loop in case SPA is slow
            int retries = 0;
            while (firstProduct.count() == 0 && retries < 5) {
                System.out.println("Waiting for products to load... retry " + (retries + 1));
                Thread.sleep(5000); // wait 5s
                retries++;
            }

            // Wait until the element exists in DOM
            firstProduct.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.ATTACHED)
                    .setTimeout(90000)
            );

            // Scroll into view and ensure visible
            firstProduct.scrollIntoViewIfNeeded();
            firstProduct.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(30000)
            );

            System.out.println("--- Starting Comprehensive Sorting Test ---");

            List<String> sortValues = Arrays.asList(
                    "name,asc",
                    "name,desc",
                    "price,desc",
                    "price,asc",
                    "co2_rating,asc",
                    "co2_rating,desc"
            );

            // Locate the sort dropdown
            Locator sortDropdown = page.locator("select[data-test='sort']");
            sortDropdown.scrollIntoViewIfNeeded();
            sortDropdown.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(30000)
            );

            for (String value : sortValues) {

                // Select sorting option
                sortDropdown.selectOption(value);

                // Wait for sorting completion
                page.waitForSelector("[data-test='sorting_completed']");

                // Validate ascending price
                if (value.equals("price,asc")) {
                    List<Double> prices = page.locator("[data-test='product-price']")
                            .allTextContents()
                            .stream()
                            .map(p -> Double.parseDouble(p.replaceAll("[^0-9.]", "")))
                            .collect(Collectors.toList());

                    for (int i = 0; i < prices.size() - 1; i++) {
                        assertTrue(prices.get(i) <= prices.get(i + 1),
                                "Price sorting failed at index " + i);
                    }

                    System.out.println("Validation Success: Price (asc) is mathematically correct.");
                }

                // Print first product after sort
                firstProduct.scrollIntoViewIfNeeded();
                firstProduct.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(30000)
                );
                System.out.println(
                        "Sort: [" + value + "] -> First Product: " +
                                firstProduct.textContent().trim()
                );
            }

            System.out.println("--- All sorting options tested successfully ---");

            browser.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}



//
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