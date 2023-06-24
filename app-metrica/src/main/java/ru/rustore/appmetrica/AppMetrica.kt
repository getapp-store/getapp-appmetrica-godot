package ru.rustore.appmetrica

import com.yandex.metrica.AdRevenue
import com.yandex.metrica.AdType
import com.yandex.metrica.PreloadInfo
import com.yandex.metrica.Revenue
import com.yandex.metrica.Revenue.Receipt
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import org.godotengine.godot.Dictionary
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.UsedByGodot
import org.json.JSONObject
import java.util.Currency


class AppMetrica(godot: Godot?) : GodotPlugin(godot) {
    lateinit var config: YandexMetricaConfig.Builder

    override fun getPluginName(): String {
        return "AppMetrica"
    }

    @UsedByGodot
    fun config(key: String) {
        config = YandexMetricaConfig.newConfigBuilder(key)
    }

    @UsedByGodot
    fun init() {
        if (godot == null) {
            return
        }

        YandexMetrica.activate(godot.requireContext(), config.build())
    }

    // config

    @UsedByGodot
    fun withAppVersion(version: String) {
        config.withAppVersion(version)
    }

    @UsedByGodot
    fun setSessionTimeout(timeout: Int) {
        config.withSessionTimeout(timeout)
    }

    @UsedByGodot
    fun withCrashReporting(enabled: Boolean) {
        config.withCrashReporting(enabled)
    }

    @UsedByGodot
    fun withNativeCrashReporting(enabled: Boolean) {
        config.withNativeCrashReporting(enabled)
    }

    @UsedByGodot
    fun withLogs() {
        config.withLogs()
    }

    @UsedByGodot
    fun withLocationTracking(enabled: Boolean) {
        config.withLocationTracking(enabled)
    }

    @UsedByGodot
    fun withPreloadInfo(name: String, params: Dictionary) {
        val info = PreloadInfo.newBuilder(name)
        for (p in params) {
            if (p.value is String) {
                info.setAdditionalParams(p.key, p.value as String)
            }
        }
        config.withPreloadInfo(info.build())
    }

    @UsedByGodot
    fun withStatisticsSending(enabled: Boolean) {
        config.withStatisticsSending(enabled)
    }

    @UsedByGodot
    fun withMaxReportsInDatabaseCount(cnt: Int) {
        config.withMaxReportsInDatabaseCount(cnt)
    }

    @UsedByGodot
    fun withUserProfileID(id: String) {
        config.withUserProfileID(id)
    }

    @UsedByGodot
    fun withRevenueAutoTrackingEnabled(enabled: Boolean) {
        config.withRevenueAutoTrackingEnabled(enabled)
    }

    @UsedByGodot
    fun withSessionsAutoTrackingEnabled(enabled: Boolean) {
        config.withSessionsAutoTrackingEnabled(enabled)
    }

    @UsedByGodot
    fun withAppOpenTrackingEnabled(enabled: Boolean) {
        config.withAppOpenTrackingEnabled(enabled)
    }

    // report

    @UsedByGodot
    fun reportError(group: String, message: String) {
        YandexMetrica.reportError(group, message)
    }

    @UsedByGodot
    fun reportEvent(name: String, params: Dictionary) {
        YandexMetrica.reportEvent(name, params.toMutableMap())
    }

    @UsedByGodot
    fun reportAdRevenue(revenue: Double, currency: String, params: Dictionary) {
        val event = AdRevenue.newBuilder(revenue, Currency.getInstance(currency))

        if (params.containsKey(AD_TYPE)) {
            val ad = when (params.get(AD_TYPE) as String) {
                "native" -> AdType.NATIVE
                "banner" -> AdType.BANNER
                "rewarded" -> AdType.REWARDED
                "interstitial" -> AdType.INTERSTITIAL
                "mrec" -> AdType.MREC
                else -> AdType.OTHER
            }
            event.withAdType(ad)
        }

        if (params.containsKey(AD_NETWORK)) {
            event.withAdNetwork(params[AD_NETWORK].toString())
        }

        if (params.containsKey(AD_UNIT_ID)) {
            event.withAdUnitId(params[AD_UNIT_ID].toString())
        }

        if (params.containsKey(AD_UNIT_NAME)) {
            event.withAdUnitName(params[AD_UNIT_NAME].toString())
        }

        if (params.containsKey(AD_PLACEMENT_ID)) {
            event.withAdPlacementId(params[AD_PLACEMENT_ID].toString())
        }

        if (params.containsKey(AD_PLACEMENT_NAME)) {
            event.withAdPlacementName(params[AD_PLACEMENT_NAME].toString())
        }

        if (params.containsKey(AD_PRECISION)) {
            event.withPrecision(params[AD_PRECISION].toString())
        }

        if (params.containsKey(PAYLOAD)) {
            val json = JSONObject(params[PAYLOAD].toString())
            val payload = json.toMap().map { e -> e.key to e.value.toString() }.toMap()

            event.withPayload(payload)
        }

        YandexMetrica.reportAdRevenue(event.build())
    }

    @UsedByGodot
    fun reportRevenue(revenue: Long, currency: String, params: Dictionary) {
        val revenue = Revenue.newBuilderWithMicros(revenue, Currency.getInstance(currency))

        if (params.containsKey(REVENUE_QUANTITY)) {
            revenue.withQuantity(params[REVENUE_QUANTITY] as? Int)
        }

        if (params.containsKey(REVENUE_PRODUCT_ID)) {
            revenue.withProductID(params[REVENUE_PRODUCT_ID].toString())
        }

        if (params.containsKey(PAYLOAD)) {
            revenue.withPayload(params[PAYLOAD].toString())
        }

        if (params.containsKey(REVENUE_RECEIPT)) {
            val receipt: Receipt.Builder = Receipt.newBuilder()
                .withData(params[REVENUE_RECEIPT].toString())
            if  (params.containsKey(REVENUE_SIGNATURE)) {
                receipt.withSignature(params[REVENUE_RECEIPT].toString())
            }

            revenue.withReceipt(receipt.build())
        }

        YandexMetrica.reportRevenue(revenue.build())
    }

    companion object {
        const val AD_NETWORK = "network"
        const val AD_TYPE = "type"
        const val AD_PRECISION = "precision"
        const val AD_PLACEMENT_ID = "placement_id"
        const val AD_PLACEMENT_NAME = "placement_name"
        const val AD_UNIT_ID = "unit_id"
        const val AD_UNIT_NAME = "unit_name"

        const val REVENUE_QUANTITY = "quantity"
        const val REVENUE_PRODUCT_ID = "product_id"
        const val REVENUE_RECEIPT = "receipt"
        const val REVENUE_SIGNATURE = "signature"

        const val PAYLOAD = "payload"
    }
}
