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

                int totalJobsInGu = driver.findElements(By.cssSelector("ul#rsList02 > li")).size();

                // 2. index 기반으로 안정적인 루프를 실행합니다.
                for (int i = 0; i < totalJobsInGu; i++) {
                    // 3. StaleElementReferenceException 방지를 위해 루프마다 리스트와 요소를 '항상' 다시 찾습니다.
                    List<WebElement> currentLiList = driver.findElements(By.cssSelector("ul#rsList02 > li"));

                    // 리스트가 예상치 못하게 비어있으면 루프를 중단합니다.
                    if (currentLiList.isEmpty() || i >= currentLiList.size()) {
                        System.out.println(gu + " - 게시글 목록이 변경되어 루프를 중단합니다.");
                        break;
                    }

                    WebElement li = currentLiList.get(i);

                    // '상세보기' 링크가 로딩될 때까지 잠시 기다립니다.
                    WebDriverWait elementWait = new WebDriverWait(driver, Duration.ofSeconds(5));
                    WebElement detailLink = elementWait.until(
                            ExpectedConditions.elementToBeClickable(li.findElement(By.cssSelector("a[title='상세보기 이동']")))
                    );
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

                        // 3개의 상세내용 블록을 하나의 문자열로 합치기 위한 StringBuilder
                        StringBuilder combinedDetails = new StringBuilder();

                        for (WebElement tblView : tblViewList) {
                            String tblViewText = tblView.getText();
                            // 각 블록 사이에 구분선을 넣어줍니다.
                            combinedDetails.append(tblViewText).append("\n--- 구분선 ---\n");
                        }

                        // 합쳐진 내용이 있을 경우에만 결과에 추가
                        if (!combinedDetails.isEmpty()) {
                            String finalDetails = combinedDetails.toString().trim();
                            // System.out.println("--- 통합된 상세 내용 ---\n" + finalDetails); // 확인용 로그

                            // 하나의 공고에 대한 정보를 담는 Map 생성
                            Map<String, String> jobInfo = new HashMap<>();
                            jobInfo.put("구", gu);
                            jobInfo.put("상세내용", finalDetails);
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