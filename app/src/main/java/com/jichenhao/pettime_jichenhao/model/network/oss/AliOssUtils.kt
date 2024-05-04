package com.jichenhao.pettime_jichenhao.model.network.oss

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.OSS
import com.alibaba.sdk.android.oss.OSSClient
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback
import com.alibaba.sdk.android.oss.common.OSSLog
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider
import com.alibaba.sdk.android.oss.common.utils.OSSUtils
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask
import com.alibaba.sdk.android.oss.model.ObjectMetadata
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult
import com.jichenhao.pettime_jichenhao.model.entity.Credentials
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

// 阿里云OSS的上传下载操作工具类

object AliOssUtils {
    /*
    accessKey、secretKey、endpoint、bucketName分别存储了
    阿里云OSS的访问密钥、密钥密码、终端节点地址和Bucket名称。
    这些值需要替换为真实有效的阿里云OSS账户信息。
    * */

    // 添加STS临时凭证属性
    // token有效期12小时，每次进入Splash页面都会重新获取
    private var stsToken: Credentials? = null

    lateinit var securityToken: String
    lateinit var accessKey: String
    lateinit var secretKey: String
    val endpoint = "oss-cn-shanghai.aliyuncs.com"
    val bucketName = "jichenhao-bucket"

    /**
     * 设置STS临时凭证
     */
    fun setStsToken(stsToken: Credentials) {
        this.stsToken = stsToken
        securityToken = AliOssUtils.stsToken?.securityToken.toString()
        accessKey = AliOssUtils.stsToken?.accessKeyId.toString()
        secretKey = AliOssUtils.stsToken?.accessKeySecret.toString()
        // test
        Log.d("阿里云OSSToken：", "accessKey:${accessKey}\n secretKey:${secretKey}")
    }

    /*
    * init代码块在对象实例化时立即执行，
    * 这里的OSSLog.enableLog()开启了阿里云OSS SDK的日志输出功能，以便调试和跟踪上传过程中的信息。
    * */
    init {
        OSSLog.enableLog() //调用此方法开启日志
    }

    /**
     * 上传多个文件
     * uploadFiles方法：
    接受一个Context对象、一个包含多个File对象的列表（代表待上传的文件集合）
     * 以及一个回调函数（接收上传成功的文件URL列表）。
    方法内遍历文件列表，对每个文件调用uploadFile方法，
     * 并在所有文件上传完毕后通过回调函数返回上传成功的文件URL列表。
     *
     * */
    fun uploadFiles(
        application: Context,
        uploadFiles: List<File>,
        callBack: (List<String>) -> Unit
    ) {
        if (uploadFiles.isEmpty()) return
        var uploadResultList = ArrayList<String>()
        var uploadFileCount = uploadFiles.size
        var uploadResultCount = AtomicInteger(0)
        uploadFiles.forEach {
            uploadFile(application, it) { url ->
                val count = uploadResultCount.addAndGet(1)
                if (url != null && !TextUtils.isEmpty(url)) {
                    uploadResultList.add(url)
                }
                if (count == uploadFileCount) {
                    callBack.invoke(uploadResultList)
                }
            }
        }
    }

    /**
     * 上传单个文件
     * uploadFile方法：
     * 主要负责单个文件的上传操作。
     * 创建了一个OSSCustomSignerCredentialProvider子类实例，重写了signContent方法，用于生成上传请求的签名字符串。
     * 注释中说明了这部分实际应该调用自己的业务服务器进行签名操作，
     * 但这里简化为直接调用了OSSUtils.sign方法
     * 根据提供的accessKey、secretKey和endpoint创建了OSS客户端实例。
     * 构建一个PutObjectRequest，包含了目标Bucket名、上传文件的key（即在OSS中的路径和名称）、本地文件路径等信息。
     * 设置元数据，如将对象设为公共读取资源。
     * 添加进度回调以实时反馈上传进度。
     * 使用asyncPutObject方法异步上传文件，并注册两个回调方法：onSuccess和onFailure。
     * 成功时，记录上传成功的相关信息，计算出公开访问的URL，并通过回调函数返回。
     * 失败时，打印异常信息，同样通过回调函数返回null表示上传失败。
     */
    fun uploadFile(
        application: Context,
        uploadFile: File,
        callBack: ((String?) -> Unit)? = null
    ) {
        // 创建STS临时凭证提供者
        val stsCredentialProvider = OSSStsTokenCredentialProvider(
            accessKey,
            secretKey,
            securityToken
        )

        val oss: OSS = OSSClient(application, endpoint, stsCredentialProvider)
        /*以前的不安全的做法
        val provider: OSSCustomSignerCredentialProvider =
            object : OSSCustomSignerCredentialProvider() {
                override fun signContent(content: String): String {
                    // 此处本应该是客户端将contentString发送到自己的业务服务器,然后由业务服务器返回签名后的content。关于在业务服务器实现签名算法
                    // 详情请查看http://help.aliyun.com/document_detail/oss/api-reference/access-control/signature-header.html。客户端
                    // 的签名算法实现请参考OSSUtils.sign(accessKey,screctKey,content)
                    return OSSUtils.sign(
                        accessKey,
                        secretKey,
                        content
                    )
                }
            }

        val oss: OSS = OSSClient(application, endpoint, provider)
        */

        // 构造上传请求。
        val put = PutObjectRequest(
            bucketName, /*objectKey*/
            uploadFile.name,/*uploadFilePath*/
            uploadFile.path
        )
        val metaData = ObjectMetadata()
        //设置为公共读资源
        metaData.setHeader("x-oss-object-acl", "public-read")
        put.metadata = metaData

        // 异步上传时可以设置进度回调。
        put.progressCallback = OSSProgressCallback { request, currentSize, totalSize ->
            Log.d(
                "PutObject",
                "currentSize: $currentSize totalSize: $totalSize"
            )
        }

        val task: OSSAsyncTask<*> = oss.asyncPutObject(
            put,
            object : OSSCompletedCallback<PutObjectRequest?, PutObjectResult> {
                override fun onSuccess(request: PutObjectRequest?, result: PutObjectResult) {
                    Log.d("PutObject", "UploadSuccess")
                    Log.d("ETag", result.eTag)
                    Log.d("RequestId", result.requestId)

                    val publicUrl =
                        "http://$bucketName.$endpoint/${uploadFile.name}"
                    Log.d("PutObject", "publicUrl:$publicUrl")
                    Log.d("ZZZZ", "publicUrl:$publicUrl")
                    callBack?.invoke(publicUrl)
                }

                override fun onFailure(
                    request: PutObjectRequest?,
                    clientExcepion: ClientException?,
                    serviceException: ServiceException?
                ) {
                    // 请求异常。
                    clientExcepion?.printStackTrace()
                    if (serviceException != null) {
                        // 服务异常。
                        Log.e("ErrorCode", serviceException.getErrorCode())
                        Log.e("RequestId", serviceException.getRequestId())
                        Log.e("HostId", serviceException.getHostId())
                        Log.e("RawMessage", serviceException.getRawMessage())
                    }
                    callBack?.invoke(null)
                }
            })
        // task.cancel(); // 可以取消任务。
        // task.waitUntilFinished(); // 等待上传完成。
    }
}
