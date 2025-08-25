package com.example.GOLDNet.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class CrawlerService {
    private static final List<String> GU_LIST = Arrays.asList(
            "강남구", "강동구", "강북구", "강서구",
            "관악구", "광진구", "구로구", "금천구", "노원구", "도봉구",
            "동대문구", "동작구", "마포구", "서대문구", "서초구", "성동구", "성북구",
            "송파구", "양천구", "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구"
    );

    public List<Map<String, String>> crawlJobs() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        List<Map<String, String>> totalResults = new ArrayList<>();

        try {
            driver.get("https://www.seniorro.or.kr:4431/noin/main.do");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(5000));

            for (String gu : GU_LIST) {
                // 1. 검색창에 구 이름 입력
                WebElement searchInput = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id("seachKeyword")));
                searchInput.clear();
                searchInput.sendKeys(gu);

                // 2. 자동완성 결과 클릭
                WebElement autoCompleteResult = wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.xpath("//a[contains(@class, 'info-link') and contains(.,'" + gu + "')]")
                        )
                );
                autoCompleteResult.click();

                // 3. 게시글 리스트 로딩 대기 및 예외 처리
                List<WebElement> liList = Collections.emptyList();
                try {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ul#rsList02 > li")));
                    liList = driver.findElements(By.cssSelector("ul#rsList02 > li"));

                    if (liList.isEmpty()) {
                        System.out.println(gu + " - 게시글 없음");
                        continue;
                    }

                } catch (TimeoutException e) {
                    System.out.println(gu + " - 게시글 없음 (Timeout)");
                    continue;
                }

                String originalHandle = driver.getWindowHandle();

                for (int i = 0; i < liList.size(); i++) {
                    // StaleElementReferenceException 방지를 위해 루프마다 리스트 재조회
                    liList = driver.findElements(By.cssSelector("ul#rsList02 > li"));
                    if (liList.isEmpty()) {
                        System.out.println(gu + " - 게시글 없음 (재조회 후)");
                        break; // 리스트가 비어있으면 루프를 종료
                    }

                    WebElement li = liList.get(i);

                    WebElement detailLink = li.findElement(By.cssSelector("a[title='상세보기 이동']"));
                    String href = detailLink.getAttribute("href");
                    ((JavascriptExecutor) driver).executeScript("window.open(arguments[0], '_blank');", href);

                    // 새 탭으로 전환
                    Set<String> handles = driver.getWindowHandles();
                    for (String handle : handles) {
                        if (!handle.equals(originalHandle)) {
                            driver.switchTo().window(handle);
                            break;
                        }
                    }

                    try {
                        WebDriverWait newTabWait = new WebDriverWait(driver, Duration.ofSeconds(10));
                        newTabWait.until(
                                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.tbl-th"))
                        );

                        // 상세 페이지 내의 모든 'tbl-view' 요소를 리스트로 찾기
                        List<WebElement> tblViewList = driver.findElements(By.cssSelector("div.tbl-view"));

                        // 각 'tbl-view' 요소의 내용을 순회하며 가져옴
                        for (WebElement tblView : tblViewList) {
                            String tblViewText = tblView.getText();
                            System.out.println("상세 내용:\n" + tblViewText);

                            // 크롤링 데이터를 저장
                            Map<String, String> jobInfo = new HashMap<>();
                            jobInfo.put("구", gu);
                            jobInfo.put("상세내용", tblViewText);
                            totalResults.add(jobInfo);
                        }

                    } catch (TimeoutException e) {
                        System.out.println("상세 내용 없음");
                    } finally {
                        driver.close();
                        driver.switchTo().window(originalHandle);
                    }

                    Thread.sleep(700);
                }
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        return totalResults;
    }
}