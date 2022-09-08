package com.footprint.footprint.service

import android.content.Context
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.footprint.footprint.BuildConfig
import com.footprint.footprint.utils.AES128
import com.footprint.footprint.utils.LogUtils
import java.io.File
import java.util.*

object S3UploadService {
    interface Callback {
        fun successUploadImg(img: String)
        fun failUploadImg()
        fun successFootprintImg(img: String, footprintIdx: Int, imgIdx: Int)
        fun failFootprintImg(footprintIdx: Int, imgIdx: Int)
    }

    private val awsCredentials: AWSCredentials = BasicAWSCredentials(decrypt(BuildConfig.s3_accesskey), decrypt(BuildConfig.s3_secretkey)) // IAM 생성하며 받은 것 입력
    private val s3Client = AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2))

    private lateinit var callback: Callback

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun uploadFootprintImg(context: Context, file: File, footprintIdx: Int, imgIdx: Int) {
        val transferUtility = TransferUtility.builder().s3Client(s3Client).context(context).build()
        TransferNetworkLossHandler.getInstance(context)

        val name: String = createFileName(file.name, file.extension)
        val uploadObserver = transferUtility.upload(decrypt(BuildConfig.s3_bucket), name, file) // (bucket api, file이름, file객체)

        uploadObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {
                if (state == TransferState.COMPLETED)
                    callback.successFootprintImg("${decrypt(BuildConfig.s3_base_url)}/$name", footprintIdx, imgIdx)
                else if (state == TransferState.FAILED)
                    callback.failFootprintImg(footprintIdx, imgIdx)
            }

            override fun onProgressChanged(id: Int, current: Long, total: Long) {
            }

            override fun onError(id: Int, ex: Exception) {
                LogUtils.d("S3UploadService", "UPLOAD ERROR - - ID: \$id - - EX:$ex")
                callback.failFootprintImg(footprintIdx, imgIdx)
            }
        })
    }

    fun uploadImg(context: Context, file: File) {
        val transferUtility = TransferUtility.builder().s3Client(s3Client).context(context).build()
        TransferNetworkLossHandler.getInstance(context)

        val name: String = createFileName(file.name, file.extension)
        val uploadObserver = transferUtility.upload(decrypt(BuildConfig.s3_bucket), name, file) // (bucket api, file이름, file객체)

        uploadObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {
                if (state == TransferState.COMPLETED)
                    callback.successUploadImg("${decrypt(BuildConfig.s3_base_url)}/$name")
                else if (state == TransferState.FAILED)
                    callback.failUploadImg()
            }

            override fun onProgressChanged(id: Int, current: Long, total: Long) {
            }

            override fun onError(id: Int, ex: Exception) {
                LogUtils.d("S3UploadService", "UPLOAD ERROR - - ID: \$id - - EX:$ex")
                callback.failUploadImg()
            }
        })
    }

    private fun createFileName(fileName: String, extension: String): String { // 먼저 파일 업로드 시, 파일명을 난수화하기 위해 random으로 돌립니다.
        return "${UUID.nameUUIDFromBytes(fileName.toByteArray(Charsets.UTF_8))}.$extension"
    }

    private fun decrypt(data: String): String {
        return AES128(BuildConfig.encrypt_key).decrypt(data)
    }
}