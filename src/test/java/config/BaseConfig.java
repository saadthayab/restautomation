package config;

import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;

import java.util.Arrays;

import static org.hamcrest.Matchers.lessThan;

public class BaseConfig {

    public static RequestSpecification reqSpec;
    public static ResponseSpecification respSpec;
    public static ExtentSparkReporter htmlReporter;
    public static ExtentReports extent;
    public static ExtentTest test;

    @BeforeTest
    public static void setup() {

        setReport();
        reqSpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setBasePath("/app/")
                .setPort(8080)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        respSpec = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectResponseTime(lessThan(7000L))
                .build();

        RestAssured.requestSpecification = reqSpec;
        RestAssured.responseSpecification = respSpec;

    }

    public static void setReport() {

        htmlReporter = new ExtentSparkReporter("./reports/DishRegressionReport.html");

        htmlReporter.config().setEncoding("utf-8");
        htmlReporter.config().setDocumentTitle("Dish API Regression Report");
        htmlReporter.config().setReportName("Automation Test Results");
        htmlReporter.config().setTheme(Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);

        extent.setSystemInfo("Automation Tester", "Saad Thayab And Robert Cui");
        extent.setSystemInfo("Orgainzation", "Dish API");
        extent.setSystemInfo("Build No", "1.0");
        extent.setSystemInfo(" Environment", "Dev");

    }

    @AfterClass
    public static void endReport() {

        extent.flush();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {

        if (result.getStatus() == ITestResult.FAILURE) {


            String failureLogg = "TEST CASE FAILED";
            Markup m = MarkupHelper.createLabel(failureLogg, ExtentColor.RED);
            test.log(Status.FAIL, m);

        } else if (result.getStatus() == ITestResult.SKIP) {

            String methodName = result.getMethod().getMethodName();

            String logText = "<b>" + "TEST CASE: - " + methodName.toUpperCase() + "  SKIPPED" + "</b>";

            Markup m = MarkupHelper.createLabel(logText, ExtentColor.YELLOW);
            test.skip(m);

        } else if (result.getStatus() == ITestResult.SUCCESS) {

            String methodName = result.getMethod().getMethodName();

            String logText = "<b>" + "TEST CASE: - " + methodName.toUpperCase() + "  PASSED" + "</b>";

            Markup m = MarkupHelper.createLabel(logText, ExtentColor.GREEN);
            test.pass(m);

        }
    }
}