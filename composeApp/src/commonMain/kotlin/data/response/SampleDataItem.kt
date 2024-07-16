package data.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SampleDataItem(
    @SerialName("albumId")
    var albumId: Int? = -1,
    @SerialName("id")
    var id: Int? = -1,
    @SerialName("thumbnailUrl")
    var thumbnailUrl: String? = null,
    @SerialName("title")
    var title: String? = null,
    @SerialName("url")
    var url: String? = null
)