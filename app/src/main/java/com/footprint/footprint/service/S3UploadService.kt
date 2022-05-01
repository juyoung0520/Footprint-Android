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
import com.footprint.footprint.utils.LogUtils
import java.io.File
import java.util.*

object S3UploadService {
    fun uploadImg(context: Context, file: File) {
        val awsCredentials: AWSCredentials = BasicAWSCredentials(BuildConfig.s3_accesskey, BuildConfig.s3_secretkey) // IAM 생성하며 받은 것 입력
        val s3Client = AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2))

        val transferUtility = TransferUtility.builder().s3Client(s3Client).context(context).build()
        TransferNetworkLossHandler.getInstance(context)

        val name: String = createFileName(file.name, file.extension)
        val uploadObserver = transferUtility.upload(BuildConfig.s3_bucket, name, file) // (bucket api, file이름, file객체)

        uploadObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {
                if (state == TransferState.COMPLETED) {
                    val url = "${BuildConfig.s3_base_url}/$name"
                }
            }

            override fun onProgressChanged(id: Int, current: Long, total: Long) {
                val done = (current.toDouble() / total * 100.0).toInt()
                LogUtils.d("S3UploadService", "UPLOAD - - ID: \$id, percent done = \$done")
            }

            override fun onError(id: Int, ex: Exception) {
                LogUtils.d("S3UploadService", "UPLOAD ERROR - - ID: \$id - - EX:$ex")
            }
        })
    }

    private fun createFileName(fileName: String, extension: String): String { // 먼저 파일 업로드 시, 파일명을 난수화하기 위해 random으로 돌립니다.
        return "${UUID.nameUUIDFromBytes(fileName.toByteArray(Charsets.UTF_8))}.$extension"
    }
}