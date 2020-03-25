package clem.app.mymvvm.ui

//import io.reactivex.Observable
//import io.reactivex.ObservableOnSubscribe
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.schedulers.Schedulers
import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Observer
import clem.app.core.base.BaseVMActivity
import clem.app.mymvvm.App
import clem.app.mymvvm.BuildConfig
import clem.app.mymvvm.R
import clem.app.mymvvm.model.bean.CovidItem
import clem.app.mymvvm.util.AverageNum
import clem.app.mymvvm.util.GreenDaoManager
import clem.app.mymvvm.util.Preference
import clem.app.mymvvm.util.ToastUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.clem.mymvvm.gen.CovidItemDao
import jonathanfinerty.once.Once
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lecho.lib.hellocharts.gesture.ContainerScrollType
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.view.LineChartView
import org.jsoup.Jsoup
import java.text.NumberFormat
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : BaseVMActivity<HomeViewModel>() {

    companion object {
        const val TAG = "MainActivity"
        const val CONFIRMED = 1
        const val DEATH = 2
        const val RECOVERED = 3
        const val COUNTRY = "country"
        const val PROVINCE = "province"
    }

    override fun providerVMClass(): Class<HomeViewModel>? = HomeViewModel::class.java

    private val handler by lazy { Handler() }

    private var country by Preference(COUNTRY, "US")
    private var province by Preference(PROVINCE, "New York")
    private var countrySelect = "US"
    private var provinceSelect = "New York"

    private val hasAxes = true
    private val hasAxesNames = true
    private val hasLines = true
    private val hasPoints = true
    private val shape = ValueShape.CIRCLE
    private val isFilled = true
    private val hasLabels = true
    private val isCubic = true
    private val hasLabelForSelected = false
    private var hasNoticed = false

    private var progress1 = ""
    private var progress2 = ""
    private var progress3 = ""
    private lateinit var timer: TimerTask

    override fun getLayoutResId() = R.layout.activity_main

    override fun initData() {
        mViewModel.getConfirmed()
        mViewModel.getDeath()
        mViewModel.getRecovered()
        renderChart()
    }

    override fun initView() {
//        ToastUtil.showLongToast(confirmedItems.size.toString()+  " " +
//                deathItems.size.toString()+  " "
//                + recoveredItems.size.toString()+  " " )

//        chart.setOnValueTouchListener(ValueTouchListener())
        refresh_iv.setOnClickListener {
            if (progress.visibility == GONE && progressTv1.visibility == GONE && progressTv2.visibility == GONE && progressTv3.visibility == GONE) {
                timer.cancel()
                timerTask()
                mViewModel.getConfirmed()
                mViewModel.getDeath()
                mViewModel.getRecovered()
                progress.visibility = VISIBLE
                progress1 = ""
                progress2 = ""
                progress3 = ""
                progressTv1.visibility = VISIBLE
                progressTv2.visibility = VISIBLE
                progressTv3.visibility = VISIBLE
            } else {
                ToastUtil.showShortToast("fetching data...")
            }
        }

        timerTask()

        area_tv.text = country
        if (!TextUtils.isEmpty(province)) {
            area_tv2.text = province
        } else {
            area_tv2.text = "--"
        }
        area.setOnClickListener {
            val countryList = GreenDaoManager.listItem(
                GreenDaoManager.getInstance().getmDaoSession(),
                "SELECT DISTINCT COUNTRY FROM COVID_ITEM"
            )
//            println(countryList.size)
            //open dialog
            MaterialDialog(this).show {
                message(text = "Please select")
                listItemsSingleChoice(
                    items = countryList,
                    initialSelection = 0,
                    waitForPositiveButton = false
                ) { dialog, index, text ->
                    countrySelect = text.toString()
                }
                positiveButton(R.string.hai, null) {
                    country = countrySelect

                    if (!TextUtils.isEmpty(country)) {
                        this@MainActivity.area_tv.text = country
                    }

                    val provinceList = GreenDaoManager.listItem(
                        GreenDaoManager.getInstance().getmDaoSession(),
                        "SELECT DISTINCT PROVINCE FROM COVID_ITEM WHERE COUNTRY = '$country'"
                    )

                    if (provinceList != null && provinceList.size > 0) {
                        if (provinceList.size == 1 && TextUtils.isEmpty(provinceList[0])) {
                            provinceSelect = ""
                            this@MainActivity.area_tv2.text = "--"
                        } else {
                            provinceSelect = provinceList[0]
                            this@MainActivity.area_tv2.text = provinceSelect
                        }
                    } else {
                        provinceSelect = ""
                        this@MainActivity.area_tv2.text = "--"
                    }

                    province = provinceSelect
                    generateData(country, province, CONFIRMED, this@MainActivity.confirm_chart)
                    generateData(country, province, DEATH, this@MainActivity.death_chart)
                    generateData(country, province, RECOVERED, this@MainActivity.recover_chart)
                }
                negativeButton(R.string.iie)
            }
        }

        area2.setOnClickListener {
            val provinceList = GreenDaoManager.listItem(
                GreenDaoManager.getInstance().getmDaoSession(),
                "SELECT DISTINCT PROVINCE FROM COVID_ITEM WHERE COUNTRY = '$country'"
            )

            if (provinceList != null && provinceList.size > 0) {
                if (provinceList.size == 1 && TextUtils.isEmpty(provinceList[0])) {

                } else {
                    //open dialog
                    MaterialDialog(this).show {
                        message(text = "Please select")
                        listItemsSingleChoice(
                            items = provinceList,
                            initialSelection = 0,
                            waitForPositiveButton = false
                        ) { dialog, index, text ->
                            provinceSelect = text.toString()
                        }
                        positiveButton(R.string.hai, null) {
                            province = provinceSelect
                            this@MainActivity.area_tv2.text = province
                            generateData(
                                country,
                                province,
                                CONFIRMED,
                                this@MainActivity.confirm_chart
                            )
                            generateData(country, province, DEATH, this@MainActivity.death_chart)
                            generateData(
                                country,
                                province,
                                RECOVERED,
                                this@MainActivity.recover_chart
                            )
                        }
                        negativeButton(R.string.iie)
                    }
                }
            } else {

            }
        }

    }

    private fun timerTask() {
        timer = Timer("SettingUp", false).schedule(100, 1000) {
            runOnUiThread {
                progressTv1.text = progress1
                progressTv2.text = progress2
                progressTv3.text = progress3

                if (progress?.visibility == VISIBLE) progress?.visibility = GONE

                if (progress1 == "100%") {
                    progressTv1.visibility = GONE
                }
                if (progress2 == "100%") {
                    progressTv2.visibility = GONE
                }
                if (progress3 == "100%") {
                    progressTv3.visibility = GONE
                }

                if (progressTv1.visibility == GONE && progressTv2.visibility == GONE && progressTv3.visibility == GONE) {
                    timer.cancel()
                }
            }
        }
    }

    private fun generateData(
        country: String,
        province: String,
        type: Int,
        chart: LineChartView
    ) {
        val qb = GreenDaoManager.getInstance().getmDaoSession()
            .covidItemDao.queryBuilder()
        val covidItems = qb.where(
            qb.and(
                CovidItemDao.Properties.Province.eq(province),
                CovidItemDao.Properties.Country.eq(country),
                CovidItemDao.Properties.Type.eq(type)
            )
        ).list()

        if (covidItems != null && covidItems.size > 0) {
            val covidItem = covidItems[0]
            var maxCount = 0

            val lines: MutableList<Line> =
                ArrayList()
            val values: MutableList<PointValue> = ArrayList()

            for (i in 0 until covidItem.dailyItems.size) {
                val dailyItem = covidItem.dailyItems[i]
                if (dailyItem.number > maxCount) {
                    maxCount = dailyItem.number
                }
                //点代表的值
                values.add(PointValue(i.toFloat(), dailyItem.number.toFloat()))
            }

            val line = Line(values)
            when (type) {
                CONFIRMED -> {
                    line.color = ChartUtils.COLORS[4]
                }
                DEATH -> {
                    line.color = ChartUtils.COLORS[5]
                }
                RECOVERED -> {
                    line.color = ChartUtils.COLORS[0]
                }
            }
            line.shape = shape
            line.isCubic = isCubic
            line.isFilled = isFilled
            line.setHasLabels(hasLabels)
            line.setHasLabelsOnlyForSelected(hasLabelForSelected)
            line.setHasLines(hasLines)
            line.setHasPoints(hasPoints)
            line.setHasGradientToTransparent(true)
            //            if (pointsHaveDifferentColor){
//                line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
//            }
            lines.add(line)
            val data = LineChartData(lines)

            if (hasAxes) {
                val axisX = Axis()
                val axisY = Axis().setHasLines(true)
                if (hasAxesNames) {
                    axisX.name = "Date"
                    when (type) {
                        CONFIRMED -> {
                            axisY.name = "Confirmed"
                        }
                        DEATH -> {
                            axisY.name = "Death"
                        }
                        RECOVERED -> {
                            axisY.name = "Recovered"
                        }
                    }
                }
                val valuesY: MutableList<AxisValue> =
                    ArrayList()
                //max 773
                if (maxCount > 4) {
                    val arr = AverageNum.averageANum(maxCount.toDouble(), 5)
                    var num = 0
                    for (j in arr.indices) {
                        num += arr[j]
                        val value = AxisValue(num.toFloat())
                        value.setLabel(num.toString())
                        valuesY.add(value)
                    }
                    axisY.values = valuesY
                } else {
                    if (maxCount == 0) maxCount = 1
                    for (i in 0..maxCount) {
                        val value = AxisValue(i.toFloat())
                        value.setLabel(i.toString())
                        valuesY.add(value)
                    }
                    axisY.values = valuesY
                }

                val valuesX: MutableList<AxisValue> =
                    ArrayList()
                for (i in 0 until covidItem.dailyItems.size) {
                    val value = AxisValue(i.toFloat())
                    val label = covidItem.dailyItems[i].date.substring(
                        0,
                        covidItem.dailyItems[i].date.length - 2
                    )
                    value.setLabel(label)
                    valuesX.add(value)
                }
                axisX.values = valuesX

                data.axisXBottom = axisX
                data.axisYLeft = axisY
            } else {
                data.axisXBottom = null
                data.axisYLeft = null
            }
            data.baseValue = Float.NEGATIVE_INFINITY
            chart.lineChartData = data
            chart.isViewportCalculationEnabled = true

//                            resetViewport();
            val v = Viewport(chart.maximumViewport)
            v.bottom = 0f
            v.top = (maxCount).toFloat()
            v.left = covidItem.dailyItems.size.toFloat() - 10f
            v.right = covidItem.dailyItems.size.toFloat()
            chart.currentViewport = v

            val vmax = Viewport(chart.maximumViewport)
            vmax.bottom = 0f
            vmax.top = (maxCount).toFloat()
            vmax.left = 0f
            vmax.right = covidItem.dailyItems.size.toFloat() - 1
            chart.maximumViewport = vmax

            chart.isInteractive = true
            chart.isZoomEnabled = false
            chart.isScrollEnabled = true
            chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL)
        } else {
            if (!hasNoticed) {
                progress.visibility = VISIBLE
                ToastUtil.showLongToast("init database...")
                hasNoticed = true
            }
        }
    }

    override fun startObserve() {
        mViewModel.apply {
            //may many data
            confirmed.observe(this@MainActivity, Observer { it ->
                //call when data changes
                getData(it, CONFIRMED)
            })

            death.observe(this@MainActivity, Observer { it ->
                //call when data changes
                getData(it, DEATH)
            })

            recovered.observe(this@MainActivity, Observer { it ->
                //call when data changes
                getData(it, RECOVERED)
            })

            confirmedErrorLiveData.observe(this@MainActivity, Observer { it ->
                //call when data changes
                Log.d("error", it)
            })
        }
    }

    @SuppressLint("CheckResult")
    private fun getData(it: String?, type: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val doc = Jsoup.parse(it)
            val elements = doc.select("[id^=LC]")
            val dateList: MutableList<String> = arrayListOf()
            val numberFormat = NumberFormat.getInstance()
            numberFormat.maximumFractionDigits = 0

            for (element in elements) {
                Log.d(TAG, "finish " + type + " - " + elements.indexOf(element))

                when (type) {
                    CONFIRMED -> {
                        if (elements.indexOf(element) == elements.size - 1) {
                            progress1 = "100%"
                        } else {
                            progress1 = numberFormat.format(
                                elements.indexOf(element).toFloat() / elements.size.toFloat() * 100
                            ) +
                                    "%"
                        }

                        Log.d(TAG, "pro: $progress1")
                    }
                    DEATH -> {
                        if (elements.indexOf(element) == elements.size - 1) {
                            progress2 = "100%"
                        } else {
                            progress2 = numberFormat.format(
                                elements.indexOf(element).toFloat() / elements.size.toFloat() * 100
                            ) + "%"
                        }
                    }
                    RECOVERED -> {
                        if (elements.indexOf(element) == elements.size - 1) {
                            progress3 = "100%"
                        } else {
                            progress3 = numberFormat.format(
                                elements.indexOf(element).toFloat() / elements.size.toFloat() * 100
                            ) +
                                    "%"
                        }
                    }
                }

                if (elements.indexOf(element) == 0) {
                    //line 1
                    val itemElements = element.select("th")
                    for (itemElement in itemElements) {
                        if (itemElement.text().contains("20") || itemElement.text()
                                .contains("21") || itemElement.text().contains("22")
                        ) {
                            Log.d("data refresh", itemElements.text())
                            dateList.add(itemElement.text())
                        }
                    }
                } else {
                    //other save data
                    val itemElements = element.select("td")
                    if (itemElements.size > 4) {
                        val province = itemElements[1].text()
                        val country = itemElements[2].text()
                        val lag = itemElements[3].text()
                        val lng = itemElements[4].text()
                        val qb = GreenDaoManager.getInstance().getmDaoSession()
                            .covidItemDao.queryBuilder()
                        val covidItems = qb.where(
                            qb.and(
                                CovidItemDao.Properties.Province.eq(province),
                                CovidItemDao.Properties.Country.eq(country),
                                CovidItemDao.Properties.Type.eq(type)
                            )
                        ).list()
                        var currentCovidItem: CovidItem
                        if (covidItems == null || covidItems.size == 0) {
                            //new item
                            currentCovidItem = CovidItem()
                            currentCovidItem.country = country
                            currentCovidItem.province = province
                            currentCovidItem.lat = lag
                            currentCovidItem.lng = lng
                            currentCovidItem.type = type
                            GreenDaoManager.getInstance().getmDaoSession().covidItemDao.save(
                                currentCovidItem
                            )
                        } else {
                            currentCovidItem = covidItems[0]
                        }

                        val numberList: MutableList<String> = arrayListOf()
                        for (itemElement in itemElements) {
                            //ignore meaningless line 1
                            if (itemElements.indexOf(itemElement) > 4) {
                                //date may be not empty while number may be empty
                                if (!TextUtils.isEmpty(itemElement.text())) {
                                    numberList.add(itemElement.text())
                                }
                            }
                        }

                        val dailyList: MutableList<CovidItem.ItemsBean> = arrayListOf()

                        for (i in 0 until numberList.size) {
                            val itemsBean = CovidItem.ItemsBean()
                            itemsBean.date = dateList[i]
                            itemsBean.number = numberList[i].toInt()
                            dailyList.add(itemsBean)
                        }

                        if (currentCovidItem.dailyItems == null || currentCovidItem.dailyItems.size == 0) {
                            currentCovidItem.dailyItems = dailyList
                        } else {
                            //list in db
                            val list = currentCovidItem.dailyItems
                            //new list (contains old data)
                            for (itembean in dailyList) {
                                if (!list.contains(itembean)) {
                                    list.add(itembean)
                                }
                            }

                            currentCovidItem.dailyItems = list
                        }

                        GreenDaoManager.getInstance().getmDaoSession().covidItemDao.update(
                            currentCovidItem
                        )
                    }
                }
            }

            withContext(Dispatchers.Main) {
                setVal(type)
            }
        }
        
    }

    private fun setVal(type: Int) {
        when (type) {
            CONFIRMED -> {
                generateData(country, province, CONFIRMED, confirm_chart)
            }
            DEATH -> {
                generateData(country, province, DEATH, death_chart)
            }
            RECOVERED -> {
                generateData(country, province, RECOVERED, recover_chart)
            }
        }
    }

    private fun renderChart() {
        generateData(country, province, CONFIRMED, confirm_chart)
        generateData(country, province, DEATH, death_chart)
        generateData(country, province, RECOVERED, recover_chart)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::timer.isInitialized) timer.cancel()
        handler.removeCallbacksAndMessages(null)
    }
}