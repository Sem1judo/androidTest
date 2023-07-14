package sem.ua.androidtest

import android.app.Application
import android.util.Log
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.onesignal.OneSignal


private const val FLYER_DEV_KEY = "fDeD5MPo3LuB9ruKKffwEW"
const val ONESIGNAL_APP_ID = "ZjVjZTcxNzgtNmY3OS00YWM4LTkzNTktYTU0ZTc1NDMwNjAw"



class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)

        // promptForPushNotifications will show the native Android notification permission prompt.
        // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
        OneSignal.promptForPushNotifications();

        AppsFlyerLib.getInstance().init(
            FLYER_DEV_KEY,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                    //Showing in logs all incoming data
                    data!!.map { Log.d("RD_", "${it.key} == ${it.value}") }
                }

                override fun onConversionDataFail(error: String?) {
                    Log.e("RD_", "error onAttributionFailure : $error")
                }

                override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                    data?.map {
                        Log.d("RD_", "onAppOpen_attribute: ${it.key} = ${it.value}")
                    }
                }

                override fun onAttributionFailure(error: String?) {
                    Log.e("RD_", "error onAttributionFailure : $error")
                }
            },
            this
        ).start(
            this,
            FLYER_DEV_KEY,
            object : AppsFlyerRequestListener {
                override fun onSuccess() {
                    Log.d("RD_", "Launch sent successfully")
                }

                override fun onError(errorCode: Int, errorDesc: String) {
                    Log.d(
                        "RD_", "Launch failed to be sent:\n" + "Error code: " + errorCode + "\n"
                                + "Error description: " + errorDesc
                    )
                }
            })
    }


}