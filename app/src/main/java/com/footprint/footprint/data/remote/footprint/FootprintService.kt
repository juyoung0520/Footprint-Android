package com.footprint.footprint.data.remote.footprint

import android.util.Log
import com.footprint.footprint.ui.walk.WalkView
import com.footprint.footprint.utils.GlobalApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FootprintService {
    private val footprintService = GlobalApplication.retrofit.create(FootprintRetrofitInterface::class.java)

    fun getFootprints(walkView: WalkView, walkIdx: Int) {
        walkView.onWalkLoading()

        footprintService.getFootprints(walkIdx).enqueue(object: Callback<GetFootprintsResponse> {
            override fun onResponse(
                call: Call<GetFootprintsResponse>,
                response: Response<GetFootprintsResponse>
            ) {
                val res = response.body()
                Log.d("FootprintService","\ngetFootprints-RES\ncode: ${res?.code}\nbody: $res")

                when (val code = res?.code) {
                    1000, 2221 -> walkView.onGetFootprintsSuccess(res?.result)
                    else -> walkView.onWalkFail(code!!, res?.message)
                }
            }

            override fun onFailure(call: Call<GetFootprintsResponse>, t: Throwable) {
                Log.e("FootprintService", "getFootprints-ERROR: ${t.message.toString()}")

                walkView.onWalkFail(5000, t.message.toString())
            }

        })
    }
}