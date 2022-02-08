package com.footprint.footprint.data.remote.footprint

import android.util.Log
import com.footprint.footprint.data.remote.walk.BaseResponse
import com.footprint.footprint.ui.walk.WalkDetailView
import com.footprint.footprint.utils.FormDataUtils
import com.footprint.footprint.utils.GlobalApplication
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FootprintService {
    private val footprintService = GlobalApplication.retrofit.create(FootprintRetrofitInterface::class.java)

    fun getFootprints(walkDetailView: WalkDetailView, walkIdx: Int) {
        footprintService.getFootprints(walkIdx).enqueue(object: Callback<GetFootprintsResponse> {
            override fun onResponse(
                call: Call<GetFootprintsResponse>,
                response: Response<GetFootprintsResponse>
            ) {
                val res = response.body()
                Log.d("FootprintService","\ngetFootprints-RES\ncode: ${res?.code}\nbody: $res")

                when (val code = res?.code) {
                    1000, 2221 -> walkDetailView.onGetFootprintsSuccess(res?.result)
                    else -> walkDetailView.onWalkDetailFail(code!!, res?.message)
                }
            }

            override fun onFailure(call: Call<GetFootprintsResponse>, t: Throwable) {
                Log.e("FootprintService", "getFootprints-ERROR: ${t.message.toString()}")

                walkDetailView.onWalkDetailFail(5000, t.message.toString())
            }

        })
    }

    fun updateFootprint(walkDetailView: WalkDetailView, footprintIdx: Int, footprintMap: HashMap<String, Any>, footprintPhoto: List<String>?) {
        walkDetailView.onWalkDetailLoading()

        Log.d("FootprintService", "updateFootprint footprintMap: $footprintMap")
        Log.d("FootprintService", "updateFootprint footprintPhoto: $footprintPhoto")

        var partMap: HashMap<String, RequestBody> = HashMap()
        var photos: ArrayList<MultipartBody.Part> = arrayListOf()

        if (footprintMap.isNotEmpty())
            partMap = FormDataUtils.getPartMap(footprintMap)

        if (footprintPhoto!=null) {
            photos = arrayListOf()

            for (photo in footprintPhoto)
                photos!!.add(FormDataUtils.prepareFilePart("photos", photo))
        }

        Log.d("FootprintService", "updateFootprint partMap: $partMap")
        Log.d("FootprintService", "updateFootprint photos: $photos")

        footprintService.updateFootprint(footprintIdx, partMap, photos).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                walkDetailView.onUpdateFootprintSuccess()
                val res = response.body()
                Log.d("FootprintService","\nupdateFootprint3-RES\ncode: ${res?.code}\nbody: $res")
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                walkDetailView.onWalkDetailFail(5000, t.message.toString())
                Log.e("FootprintService", "updateFootprint3-ERROR: ${t.message.toString()}")
            }

        })
    }
}