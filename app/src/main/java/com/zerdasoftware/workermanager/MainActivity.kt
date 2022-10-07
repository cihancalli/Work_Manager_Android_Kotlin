package com.zerdasoftware.workermanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.work.*
import com.zerdasoftware.workermanager.Worker.MyWorker
import com.zerdasoftware.workermanager.Worker.RefreshDatabase
import com.zerdasoftware.workermanager.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var design : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        design = ActivityMainBinding.inflate(layoutInflater)
        setContentView(design.root)

        // WORK MANAGER INIT
        val data = Data.Builder().putInt("intKey",1).build()
        val constraints = Constraints.Builder()
            //.setRequiredNetworkType(NetworkType.CONNECTED)
            //.setRequiresCharging(false)
            .build()

        // BUTTON SETUP
        buttonInit(constraints,data)
    }

    private fun buttonInit(constraints: Constraints, data: Data) {
        design.btnOneWorkStart.setOnClickListener {
            oneTimeRequest(constraints,data)
        }

        design.btnPeriodicWorkStart.setOnClickListener {
            onePeriodicRequest(constraints,data)
        }

        design.btnChainingWorkStart.setOnClickListener {
            chainingRequest(constraints,data)
        }

        design.btnStop.setOnClickListener {
            WorkManager.getInstance(this).cancelAllWork()
        }
    }

    private fun oneTimeRequest(constraints: Constraints, data: Data) {
        val workRequest : WorkRequest = OneTimeWorkRequestBuilder<MyWorker>()
            .setConstraints(constraints)
            .setInputData(data)
            .setInitialDelay(10,TimeUnit.SECONDS)
            .addTag("MyWorker")
            .build()

        workInfoByIdLiveData(workRequest, "workRequest")

        WorkManager.getInstance(this).enqueue(workRequest)
    }

    private fun onePeriodicRequest(constraints: Constraints, data: Data) {
        val periodicWorkRequest : PeriodicWorkRequest = PeriodicWorkRequestBuilder<RefreshDatabase>(15,TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setInputData(data)
            .setInitialDelay(10,TimeUnit.SECONDS)
            .addTag("myTag")
            .build()

        workInfoByIdLiveData(periodicWorkRequest, "periodicWorkRequest")


        WorkManager.getInstance(this).enqueue(periodicWorkRequest)
    }

    private fun chainingRequest(constraints: Constraints, data: Data) {
        val oneTimeWorkRequest : OneTimeWorkRequest = OneTimeWorkRequestBuilder<RefreshDatabase>()
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        WorkManager.getInstance(this)
            .beginWith(oneTimeWorkRequest)
            .then(oneTimeWorkRequest)
            .then(oneTimeWorkRequest)
            .enqueue()

        workInfoByIdLiveData(oneTimeWorkRequest,"oneTimeWorkRequest")
    }

    private fun workInfoByIdLiveData(myWorkRequest: WorkRequest, S: String) {
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(myWorkRequest.id).observe(this,
            Observer {
                when (it.state) {
                    WorkInfo.State.RUNNING -> {
                        println("$S: RUNNING")
                    }
                    WorkInfo.State.FAILED -> {
                        println("$S: FAILED")
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        println("$S: SUCCEEDED")
                    }
                    else -> {
                        println("$S: UNKNOWN ERROR")
                    }
                }
            })
    }
}