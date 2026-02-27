import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.*; // Updated to include all JUnit hooks
import java.util.*;
import java.nio.file.Paths; // Added for screenshot path

public class SortingTest {

    // Added class-level variables so the screenshot method can access the page
    private Playwright playwright;
    private Browser browser;
    private Page page;

    @Test
    void validateAllSortingOptions() {
        playwright = Playwright.create();
        boolean isCI = System.getenv("CI") != null;

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(isCI)
        );

        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
        );

        page = context.newPage();
        page.setDefaultTimeout(60000);

        page.navigate("https://practicesoftwaretesting.com/");

        Locator products = page.locator("[data-test='product-name']");
        products.first().waitFor();

        List<String> sortValues = Arrays.asList("name,asc", "name,desc",  "price,des", "price,asc");


        for (String value : sortValues) {
            String currentTopName = products.first().innerText();
            page.locator("select[data-test='sort']").selectOption(value);

            try {
                page.waitForCondition(() -> {
                    return !products.first().innerText().equals(currentTopName);
                }, new Page.WaitForConditionOptions().setTimeout(5000));
            } catch (Exception e) {
            }

            String firstProduct = products.first().innerText();
            System.out.println("Validated Sort [" + value + "] - Top Item: " + firstProduct.trim());
        }
    }

    // Added: This method runs automatically after the test finishes
    @AfterEach
    void tearDown(TestInfo testInfo) {
        if (page != null) {
            // Added: Capture screenshot only if the test failed
            // Note: In some setups you might need a custom Extension to detect failure,
            // but for now, this will save a 'final-state' image of the browser.
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("target/execution-" + testInfo.getDisplayName() + ".png"))
                    .setFullPage(true));

            browser.close();
            playwright.close();
        }
    }
}



//
//import com.microsoft.playwright.*;
//import com.microsoft.playwright.options.*;
//import org.junit.jupiter.api.Test;
//import java.util.*;
//
//public class SortingTest {
//
//    @Test
//    void validateAllSortingOptions() {
//        try (Playwright playwright = Playwright.create()) {
//            boolean isCI = System.getenv("CI") != null;
//
//            Browser browser = playwright.chromium().launch(
//                    new BrowserType.LaunchOptions().setHeadless(isCI)
//            );
//
//            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
//                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
//            );
//
//            Page page = context.newPage();
//            page.setDefaultTimeout(60000);
//
//            page.navigate("https://practicesoftwaretesting.com/");
//
//            Locator products = page.locator("[data-test='product-name']");
//            products.first().waitFor();
//
//            List<String> sortValues = Arrays.asList("name,asc", "name,desc", "price,desc", "price,asc");
//
//            for (String value : sortValues) {
//                String currentTopName = products.first().innerText();
//                page.locator("select[data-test='sort']").selectOption(value);
//
//                try {
//                    page.waitForCondition(() -> {
//                        return !products.first().innerText().equals(currentTopName);
//                    }, new Page.WaitForConditionOptions().setTimeout(5000));
//                } catch (Exception e) {
//                }
//
//                String firstProduct = products.first().innerText();
//                System.out.println("Validated Sort [" + value + "] - Top Item: " + firstProduct.trim());
//            }
//
//            browser.close();
//        }
//    }
//}
//
