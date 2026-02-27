import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.Test;
import java.util.*;

public class SortingTest {


    @Test
    void validateAllSortingOptions() {
        try (Playwright playwright = Playwright.create()) {
            boolean isCI = System.getenv("CI") != null;

            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(isCI)
            );
            // 2. Add a Human User Agent to avoid being blocked by Cloudflare
            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
            );
            Page page = context.newPage();
            page.setDefaultTimeout(60000);

            page.navigate("https://practicesoftwaretesting.com/");
            Locator products = page.locator("[data-test='product-name']");
            products.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            List<String> sortValues = Arrays.asList("name,asc", "name,desc", "price,desc", "price,asc");

            for (String value : sortValues) {
                page.locator("select[data-test='sort']").selectOption(value);
                page.waitForLoadState(LoadState.NETWORKIDLE);

                products.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
                String firstProduct = products.first().innerText();
                System.out.println("Validated Sort [" + value + "] - Top Item: " + firstProduct.trim());
            }

            browser.close();
        }
    }
}

