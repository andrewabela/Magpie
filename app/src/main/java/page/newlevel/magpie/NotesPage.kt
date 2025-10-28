package page.newlevel.magpie

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import page.newlevel.notes.storage.Note
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.viewinterop.AndroidView
import page.newlevel.magpie.R
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.asPaddingValues
import android.graphics.drawable.GradientDrawable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.only

val lexendDeca = FontFamily(
    Font(R.font.lexend_deca_extralight, androidx.compose.ui.text.font.FontWeight.ExtraLight),
    Font(R.font.lexend_deca_light, androidx.compose.ui.text.font.FontWeight.Light),
    Font(R.font.lexend_deca_normal, androidx.compose.ui.text.font.FontWeight.Normal),
    Font(R.font.lexend_deca_medium, androidx.compose.ui.text.font.FontWeight.Medium),
    Font(R.font.lexend_deca_semibold, androidx.compose.ui.text.font.FontWeight.SemiBold)
)
private val storage = page.newlevel.magpie.storage.Faker()

@Composable
internal fun MainScreen() {
    Scaffold(
        bottomBar = {
            ActionButtons()
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.notes_bg)),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).padding(WindowInsets.systemBars.only(WindowInsetsSides.Top).asPaddingValues()).padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                SettingsBtn()
                Greeting(name = "Android ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})")
                NotesListScreen()
            }
        }
    }
}

@Composable
private fun Greeting(name: String) {
    Column {
        Text(
            text = "Hello,",
            fontFamily = lexendDeca,
            fontSize = 54.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
            color = colorResource(R.color.notes_foreground_text)
        )
        Text(
            text = "$name!",
            fontFamily = lexendDeca,
            fontSize = 36.sp,
            lineHeight = 40.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
            color = colorResource(R.color.notes_foreground_text)
        )
    }
}

@Composable
private fun ActionButtons() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
    AndroidView(
        factory = { ctx ->
            android.widget.LinearLayout(ctx).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                val newBtn = android.widget.ImageView(ctx).apply {
                    setPadding(55, 55, 55, 55)
                    setImageResource( R.drawable.add)
                    setBackground(GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(ctx.getColor(R.color.action_bar_main_btn_bg))
                    })
                    setOnClickListener { newNote(ctx) }
                }
                val checkListBtn = android.widget.ImageView(ctx).apply {
                    setPadding(55, 55, 55, 55)
                    setImageResource( R.drawable.check_box)
                    setBackground(GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(ctx.getColor(R.color.action_bar_secondary_btn_bg))
                    })
                    layoutParams = android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                        setMargins(8, 0, 8, 0)
                    }
                }
                val voiceBtn = android.widget.ImageView(ctx).apply {
                    setPadding(55, 55, 55, 55)
                    setImageResource( R.drawable.mic_alt )
                    setBackground(GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(ctx.getColor(R.color.action_bar_secondary_btn_bg))
                    })

                }
                addView(newBtn)
                addView(checkListBtn)
                addView(voiceBtn)
            }
        },
        modifier = Modifier
            .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = androidx.compose.foundation.layout.WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 10.dp)
            .background(
                color = colorResource(R.color.action_bar_FAB_bg),
                shape = CircleShape
            )
    )
    }
}

private fun newNote(context: android.content.Context) {
    var newNote = storage.createNote()
    openNote(
        note = newNote,
        context = context
    )
}

private fun openNote(note: Note, context: android.content.Context) {
    val intent = android.content.Intent(context, page.newlevel.magpie.NotePage::class.java)
    NotePage.currentNote = note
    context.startActivity(intent)
}

@Composable
private fun Note(note: Note, modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Card(
        modifier = modifier.padding(2.dp).height(190.dp).clickable {
            openNote(
                note = note,
                context = context
            )
        },
        shape = RoundedCornerShape(34.dp, 34.dp, 17.dp, 34.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(
            when (kotlin.math.abs(note.hashCode()) % 9) {
                0 -> R.color.notes_card_bg_0
                1 -> R.color.notes_card_bg_1
                2 -> R.color.notes_card_bg_2
                3 -> R.color.notes_card_bg_3
                4 -> R.color.notes_card_bg_4
                5 -> R.color.notes_card_bg_5
                6 -> R.color.notes_card_bg_6
                7 -> R.color.notes_card_bg_7
                8 -> R.color.notes_card_bg_8
                else -> R.color.black
            }
        ))
    ) {
        FavoriteBtn(
            note = note
        )
        Text(
            text = "${note.getTitle()}",
            fontFamily = lexendDeca,
            fontSize = 25.sp,
            lineHeight = 30.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
            modifier = Modifier.padding(10.dp, 0.dp, 5.dp, 0.dp),
        )
    }
}

@Composable
private fun NotesListScreen() {
    val (allNotes, _) = storage.listNotes(0, 100)

    Column (
        modifier = Modifier.padding(2.dp)
    ) {
        allNotes.chunked(2).forEach { pair ->
            Row {
                pair.forEach { note ->
                    Note(note = note, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}


@Composable
private fun FavoriteBtn(note: Note) {
    val isFavoriteState = androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(note.isFavorite())
    }
    val isFavorite = isFavoriteState.value

    AndroidView(
        factory = { ctx ->
            android.widget.ImageView(ctx).apply {
                val fav = isFavoriteState.value
                setPadding(if (fav) 28 else 90, 31, if (fav) 90 else 28, 25)
                setImageResource(if (fav) R.drawable.heart_empty else R.drawable.heart_smile)
                setOnClickListener {
                    val newValue = !isFavoriteState.value
                    note.setFavorite(newValue)
                    isFavoriteState.value = newValue
                    setImageResource(if (newValue) R.drawable.heart_empty else R.drawable.heart_smile)
                    setPadding(if (newValue) 28 else 90, 31, if (newValue) 90 else 28, 25)
                }
            }
        },
        modifier = Modifier
            .padding(10.dp, 10.dp, 10.dp, 0.dp)
            .background(
                color = colorResource(R.color.semi_transparent),
                shape = CircleShape
            )
    )
}

@Composable
private fun SettingsBtn() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopEnd
    ) {
        AndroidView(
            factory = { ctx ->
                android.widget.ImageView(ctx).apply {
                    setPadding(34, 34, 34, 34)
                    setImageResource( R.drawable.settings )
                    setBackground(GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(ctx.getColor(R.color.settings_btn_icon_bg))
                    })
                    setOnClickListener {

                    }
                }
                },
                modifier = Modifier.padding(10.dp)
        )
    }
}