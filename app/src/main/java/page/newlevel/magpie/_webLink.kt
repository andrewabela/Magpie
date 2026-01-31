package page.newlevel.magpie

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.produceState
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.core.net.toUri

private data class WebLinkData(val title: String, val imageUrl: String?)

@Composable
internal fun WebLink(url: String, delFun:() -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val webData = produceState(initialValue = WebLinkData(url, null), key1 = url) {
        value = withContext(Dispatchers.IO) {
            try {
                val document = Jsoup.connect(url)
                    .timeout(3000)
                    .get()
                val title = document.title().ifBlank {
                    document.select("meta[property=og:title]").attr("content").ifBlank { url }
                }
                val image = document.select("meta[property=og:image]").attr("content")
                WebLinkData(title, if (image.isNotBlank()) image else null)
            } catch (_: Exception) {
                WebLinkData(url, null)
            }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(34.dp, 17.dp, 17.dp, 34.dp))
            .background(Color.White.copy(alpha = 0.5f))
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                context.startActivity(intent)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = webData.value.imageUrl ?: "https://www.google.com/s2/favicons?domain=$url&sz=128",
            contentDescription = null,
            modifier = Modifier
                .width(150.dp)
                .fillMaxHeight()
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )


        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
            Text(
                text = webData.value.title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(id = R.drawable.delete),
                contentDescription = "Delete Link",
                modifier = Modifier.padding(4.dp).clickable {
                    delFun()
                }.size(21.dp)
            )
            }
            Text(
                text = url,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Blue
            )
        }
    }
}

@Composable
fun WebLinkPreview() {
    WebLink("https://open.spotify.com/episode/2pIYFLVVMIZZlklXnq524O?si=BBRqCM2aTmSX4GWWJjaSyw", delFun = {})
}