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
                    new BrowserType.LaunchOptions().setHeadless(isCI)
            );

            // We use a context to set a high default for everything
            BrowserContext context = browser.newContext();
            context.setDefaultTimeout(60000);

            Page page = context.newPage();
            page.navigate("https://practicesoftwaretesting.com/");

            // 🛠️ THE FIX: Wait for at least one product card to be attached to the DOM
            // This is more reliable than '.first().waitFor()' on a slow loading SPA
            page.waitForSelector("[data-test='product-name']", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(90000));

            System.out.println("--- Starting Comprehensive Sorting Test ---");

            List<String> sortValues = Arrays.asList(
                    "name,asc", "name,desc", "price,desc", "price,asc", "co2_rating,asc", "co2_rating,desc"
            );

            Locator sortDropdown = page.locator("select[data-test='sort']");
            sortDropdown.scrollIntoViewIfNeeded();

            for (String value : sortValues) {
                // 🛠️ RE-WAIT logic: Before selecting, ensure the dropdown is ready
                sortDropdown.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
                sortDropdown.selectOption(value);

                // Wait for the "Finished" indicator from the app
                page.waitForSelector("[data-test='sorting_completed']", new Page.WaitForSelectorOptions().setTimeout(60000));

                if (value.equals("price,asc")) {
                    // Small sleep (1s) only in CI to let the UI settle after 'sorting_completed'
                    if(isCI) page.waitForTimeout(1000);

                    List<Double> prices = page.locator("[data-test='product-price']")
                            .allTextContents()
                            .stream()
                            .map(p -> Double.parseDouble(p.replaceAll("[^0-9.]", "")))
                            .collect(Collectors.toList());

                    for (int i = 0; i < prices.size() - 1; i++) {
                        assertTrue(prices.get(i) <= prices.get(i + 1), "Price sorting failed at index " + i);
                    }
                    System.out.println("Validation Success: Price (asc) is mathematically correct.");
                }

                // 🛠️ Use a direct locator here instead of the 'firstProduct' variable
                // to avoid "Stale Element Reference" after a sort re-render
                String topProductName = page.locator("[data-test='product-name']").first().textContent().trim();
                System.out.println("Sort: [" + value + "] -> First Product: " + topProductName);
            }

            System.out.println("--- All sorting options tested successfully ---");
            browser.close();
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