package ru.rustore.appmetrica

import com.yandex.metrica.AdRevenue
import com.yandex.metrica.AdType
import com.yandex.metrica.PreloadInfo
import com.yandex.metrica.Revenue
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.UsedByGodot
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
    fun withPreloadInfo(name: String, params: org.godotengine.godot.Dictionary) {
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
    fun reportEvent(name: String, params: org.godotengine.godot.Dictionary) {
        YandexMetrica.reportEvent(name, params.toMutableMap())
    }

    @UsedByGodot
    fun reportAdRevenue(revenue: Double, currency: String, params: org.godotengine.godot.Dictionary) {
        val event = AdRevenue.newBuilder(revenue, Currency.getInstance(currency))

        if (params.containsKey(AD_NETWORK)) {
            event.withAdNetwork(params[AD_NETWORK].toString())
        }

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

        if (params.containsKey(AD_PRECISION)) {
            event.withPrecision(params.get(AD_PRECISION) as String)
        }

//        event.withAdPlacementId()
//        event.withAdPlacementName()
//        event.withAdUnitId()
//        event.withAdUnitName()
//        event.withPayload()

        YandexMetrica.reportAdRevenue(event.build())
    }

    @UsedByGodot
    fun reportRevenue() {
        val revenue = Revenue.newBuilderWithMicros(99000000, Currency.getInstance("RUB"))
            .withProductID("com.yandex.service.299")
            .withQuantity(2) // Passing the OrderID parameter in the .withPayload(String payload) method to group purchases.
            .withPayload("{\"OrderID\":\"Identifier\", \"source\":\"Google Play\"}")
            .build()

        YandexMetrica.reportRevenue(revenue)
    }

    companion object {
        const val AD_NETWORK = "network"
        const val AD_TYPE = "type"
        const val AD_PRECISION = "precision"
    }
}