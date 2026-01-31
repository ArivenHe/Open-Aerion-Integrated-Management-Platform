package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.common.Result;
import cn.ariven.openaimpbackend.dto.request.common.RequestAtisInfo;
import cn.ariven.openaimpbackend.dto.request.common.RequestMetar;
import cn.ariven.openaimpbackend.dto.response.common.ResponseAtisInfo;
import cn.ariven.openaimpbackend.vo.VOAtisInfo;
import cn.ariven.openaimpbackend.dto.response.common.ResponseMetar;
import cn.ariven.openaimpbackend.service.WeatherService;
import io.github.mivek.enums.CloudQuantity;
import io.github.mivek.enums.CloudType;
import io.github.mivek.exception.ParseException;
import io.github.mivek.internationalization.Messages;
import io.github.mivek.model.Cloud;
import io.github.mivek.model.Metar;
import io.github.mivek.service.MetarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class WeatherServiceImpl implements WeatherService {

    @Override
    public Result<ResponseMetar> getMetar(RequestMetar requestMetar) {
        return getFormatMetar(requestMetar.getIcao());
    }

    private Result<ResponseMetar> getFormatMetar(String icao) {
        return formatMetar(getMetarText(icao));
    }

    private String getMetarText(String icao) {
        String url = "https://metar.vatsim.net/" + icao;

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String metar = response.body();
                log.info("获取METAR成功:{}", metar);
                return metar;
            } else {
                log.warn("获取METAR失败:{}", icao);
                return null;
            }
        } catch (Exception e) {
            log.warn("获取METAR失败:{}", icao);
            e.printStackTrace();
            return null;
        }
    }

    private Result<ResponseMetar> formatMetar(String metarText) {
        try {
            MetarService metarService = MetarService.getInstance();
            Messages.getInstance().setLocale(Locale.ENGLISH);
            Metar metar = metarService.decode(metarText);

            ResponseMetar responseMetar = ResponseMetar.builder()
                    .icao(metar.getAirport().getIcao())
                    .airportName(metar.getAirport().getName())
                    .time(metar.getTime().toString())
                    .windDir(metar.getWind().getDirectionDegrees())
                    .windSpeed(metar.getWind().getSpeed())
                    .windUnit(metar.getWind().getUnit())
                    .visibility(metar.getVisibility().getMainVisibility())
                    .visibilityUnit("meters")
                    .temperature(metar.getTemperature())
                    .dewPoint(metar.getDewPoint())
                    .qnh(metar.getAltimeter())
                    .qnhUnit("hpa")
                    .build();
            log.info("格式化METAR成功:{}", responseMetar);
            return Result.success(responseMetar);
        } catch (Exception e) {
            log.info("无数据或输入出错: {}", e.getMessage());
            return Result.failed("获取METAR失败");
        }

    }

    private final String[] PHONETIC_ALPHABET = {
            "Alpha", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot", "Golf", "Hotel",
            "India", "Juliet", "Kilo", "Lima", "Mike", "November", "Oscar", "Papa",
            "Quebec", "Romeo", "Sierra", "Tango", "Uniform", "Victor", "Whiskey",
            "X-ray", "Yankee", "Zulu"
    };

    @Override
    public Result<ResponseAtisInfo> getAtis(RequestAtisInfo requestAtisInfo) throws ParseException {
        try {
            String icao = requestAtisInfo.getIcao();
            String code = getMetarText(icao);
            if (code.startsWith("Error")) {
                return Result.failed("获取METAR失败: " + code);
            }

            Metar metar = MetarService.getInstance().decode(code);

            // --- 1. 基础数据准备 ---
            String airportName = metar.getAirport().getName() != null ? metar.getAirport().getName() : icao;
            String atisCode = PHONETIC_ALPHABET[metar.getTime().getHour() % 26];
            String timeStr = String.format("%02d%02d", metar.getTime().getHour(), metar.getTime().getMinute());
            String windDir = metar.getWind().getDirectionDegrees() == null ? "VRB" : String.format("%03d", metar.getWind().getDirectionDegrees());
            String speedStr = String.format("%02d", metar.getWind().getSpeed());
            String unit = metar.getWind().getUnit();

            // --- 2. 处理云层逻辑 (结构化 + 文案) ---
            List<VOAtisInfo.CloudInfo> cloudInfoList = new ArrayList<>();
            StringBuilder cloudCn = new StringBuilder();
            StringBuilder cloudEn = new StringBuilder();

            if (metar.isCavok()) {
                cloudCn.append("CAVOK，");
                cloudEn.append("CAVOK, ");
            } else {
                cloudCn.append("能见度 ").append(metar.getVisibility().getMainVisibility()).append("，");
                cloudEn.append("visibility ").append(metar.getVisibility().getMainVisibility()).append(", ");

                if (metar.getClouds() != null && !metar.getClouds().isEmpty()) {
                    for (Cloud cloud : metar.getClouds()) {
                        // 填充列表数据
                        cloudInfoList.add(VOAtisInfo.CloudInfo.builder()
                                .quantity(cloud.getQuantity() != null ? cloud.getQuantity().name() : null)
                                .height(cloud.getHeight())
                                .type(cloud.getType() != null ? cloud.getType().name() : null)
                                .build());

                        // 拼接文案逻辑
                        appendCloudText(cloud, cloudCn, cloudEn);
                    }
                }
            }

            // --- 3. 气温格式化 ---
            String tCn = formatTempCN(metar.getTemperature());
            String tEn = formatTempEN(metar.getTemperature());
            String dCn = formatTempCN(metar.getDewPoint());
            String dEn = formatTempEN(metar.getDewPoint());

            // --- 4. 组装最终文案 ---
            String fullCn = String.format("%s情报通播 %s，%s UTC，地面风向 %s 度，风速 %s %s，%s温度 %s 摄氏度，露点 %s 摄氏度，修正海压 %d hPa，首次与管制员联络时报告你已收到通波 %s。",
                    airportName, atisCode, timeStr, windDir, speedStr, unit, cloudCn, tCn, dCn, metar.getAltimeter(), atisCode);

            String fullEn = String.format("%s information %s, %s UTC, wind %s degrees at %s %s, %stemperature %s degree Celsius, dew point %s degree Celsius, corrected altimeter setting %d hPa, advise on initial contact you have information %s.",
                    airportName, atisCode, timeStr, windDir, speedStr, unit, cloudEn, tEn, dEn, metar.getAltimeter(), atisCode);

            // --- 5. 返回 Builder 结果 ---
            VOAtisInfo voAtisInfo = VOAtisInfo.builder()
                    .icao(icao)
                    .airportName(airportName)
                    .atisCode(atisCode)
                    .timeUtc(timeStr)
                    .windDirection(windDir)
                    .windSpeed(metar.getWind().getSpeed())
                    .windUnit(unit)
                    .isCavok(metar.isCavok())
                    .visibility(metar.isCavok() ? "CAVOK" : metar.getVisibility().getMainVisibility())
                    .clouds(cloudInfoList)
                    .temperature(metar.getTemperature())
                    .dewPoint(metar.getDewPoint())
                    .qnh(metar.getAltimeter())
                    .atisFullCn(fullCn)
                    .atisFullEn(fullEn)
                    .build();

            ResponseAtisInfo responseAtisInfo = ResponseAtisInfo.builder()
                    .code(voAtisInfo.getAtisCode())
                    .en(voAtisInfo.getAtisFullEn())
                    .cn(voAtisInfo.getAtisFullCn())
                    .build();


            return Result.success(responseAtisInfo);
        } catch (Exception e) {
            return Result.failed("获取报文失败" + e.getMessage());
        }
    }

    /**
     * 辅助方法：处理云层文本拼接
     */
    private void appendCloudText(Cloud cloud, StringBuilder cn, StringBuilder en) {
        CloudQuantity q = cloud.getQuantity();
        if (q == null) return;

        String qCn = "", qEn = "";
        switch (q) {
            case FEW:
                qCn = "少云";
                qEn = "Few";
                break;
            case SCT:
                qCn = "疏云";
                qEn = "Scattered";
                break;
            case BKN:
                qCn = "多云";
                qEn = "Broken";
                break;
            case OVC:
                qCn = "阴天";
                qEn = "Overcast";
                break;
            case SKC:
                qCn = "天空晴朗";
                qEn = "Sky Clear";
                break;
            case NSC:
                qCn = "无明显云";
                qEn = "No Significant Clouds";
                break;
        }

        if (q == CloudQuantity.SKC || q == CloudQuantity.NSC) {
            cn.append(qCn).append("，");
            en.append(qEn).append(", ");
        } else {
            String typeCn = (cloud.getType() == CloudType.CB) ? "积雨云" : (cloud.getType() == CloudType.TCU ? "塔状积云" : "");
            String typeEn = (cloud.getType() != null) ? cloud.getType().name() : "";

            cn.append(qCn).append(" ").append(cloud.getHeight()).append(" 英尺 ").append(typeCn).append("，");
            en.append(qEn).append(" ").append(cloud.getHeight()).append(" feet ").append(typeEn).append(", ");
        }
    }

    /**
     * 格式化中文温度
     * 例如：5 -> "5", -5 -> "零下 5"
     */
    private String formatTempCN(int temp) {
        if (temp < 0) {
            return "零下 " + Math.abs(temp);
        }
        return String.valueOf(temp);
    }

    /**
     * 格式化英文温度
     * 例如：5 -> "5", -5 -> "minus 5"
     */
    private String formatTempEN(int temp) {
        if (temp < 0) {
            return "minus " + Math.abs(temp);
        }
        return String.valueOf(temp);
    }
}
