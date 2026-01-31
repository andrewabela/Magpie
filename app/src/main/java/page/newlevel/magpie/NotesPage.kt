package page.newlevel.magpie

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import page.newlevel.notes.storage.Note
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.asPaddingValues
import android.graphics.drawable.GradientDrawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.only
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.fragment.app.FragmentActivity

val lexendDeca = FontFamily(
    Font(R.font.lexend_deca_extralight, androidx.compose.ui.text.font.FontWeight.ExtraLight),
    Font(R.font.lexend_deca_light, androidx.compose.ui.text.font.FontWeight.Light),
    Font(R.font.lexend_deca_normal, androidx.compose.ui.text.font.FontWeight.Normal),
    Font(R.font.lexend_deca_medium, androidx.compose.ui.text.font.FontWeight.Medium),
    Font(R.font.lexend_deca_semibold, androidx.compose.ui.text.font.FontWeight.SemiBold)
)

@Composable
internal fun MainScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val storage = page.newlevel.magpie.storage.LocalDB(context)

    // State to trigger recomposition when activity resumes
    var refreshKey by remember { mutableIntStateOf(0) }

    // Observe lifecycle to refresh notes when returning to this screen
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Increment refresh key to trigger recomposition
                refreshKey++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Get user name from SharedPreferences, updating on refresh
    val userName = remember(refreshKey) {
        SettingsActivity.getUserName(context)
    }

    Scaffold(
        bottomBar = {
            ActionButtons(storage)
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
                Greeting(name = userName)
                NotesListScreen(storage, refreshKey)
                SettingsBtn()
            }
        }
    }
}

@Composable
private fun Greeting(name: String?) {
    val greetingText = if (name.isNullOrBlank()) {
        "Android ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})"
    } else {
        name
    }

    Column {
        Text(
            text = "Hello,",
            fontFamily = lexendDeca,
            fontSize = 54.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
            color = colorResource(R.color.notes_foreground_text)
        )
        Text(
            text = "$greetingText!",
            fontFamily = lexendDeca,
            fontSize = 36.sp,
            lineHeight = 40.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
            color = colorResource(R.color.notes_foreground_text)
        )
    }
}

@Composable
private fun ActionButtons(storage: page.newlevel.notes.storage.StorageAbstract) {
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
                    setOnClickListener { newNote(ctx, storage) }
                    layoutParams = android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                        setMargins(0, 0, 5, 0)
                    }
                }
                val pasteFromClipBoardBtn = android.widget.ImageView(ctx).apply {
                    setPadding(55, 55, 55, 55)
                    setImageResource( R.drawable.clipboard)
                    setBackground(GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(ctx.getColor(R.color.action_bar_secondary_btn_bg))
                    })
                    setOnClickListener { newNoteFromClipboard(ctx, storage) }
                    layoutParams = android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                        setMargins(5, 0, 0, 0)
                    }
                }
                addView(newBtn)
                addView(pasteFromClipBoardBtn)
            }
        },
        modifier = Modifier
            .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 10.dp)
            .background(
                color = colorResource(R.color.action_bar_FAB_bg),
                shape = CircleShape
            )
    )
    }
}

private fun newNote(context: android.content.Context, storage: page.newlevel.notes.storage.StorageAbstract) {
    val newNote = storage.createNote()
    openNote(
        note = newNote,
        context = context
    )
}

private fun newNote(context: android.content.Context, storage: page.newlevel.notes.storage.StorageAbstract, content: String) {
    val newNote = storage.createNote()
    newNote.editContent(content)
    openNote(
        note = newNote,
        context = context
    )
}

private fun newNoteFromClipboard(context: android.content.Context, storage: page.newlevel.notes.storage.StorageAbstract){
    try {
        val clipboard =
            context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clipData = clipboard.primaryClip
        val clipText = clipData?.getItemAt(0)?.coerceToText(context).toString()
        if ((clipText.isEmpty() || clipText.isBlank()) || clipText == "null") {
            android.widget.Toast.makeText(context, "Clipboard is empty", android.widget.Toast.LENGTH_SHORT).show()
            return
        }
        newNote(context, storage, clipText)
    }catch (e: Exception){
        android.widget.Toast.makeText(context, "Failed to get clipboard content", android.widget.Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }
}

private fun openNote(note: Note, context: android.content.Context) {
    val intent = android.content.Intent(context, NotePage::class.java)
    NotePage.currentNote = note
    context.startActivity(intent)
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Note(note: Note, modifier: Modifier = Modifier, onNoteDeleted: () -> Unit = {}) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val showDeleteDialog = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Card(
        modifier = modifier
            .padding(2.dp)
            .height(190.dp)
            .combinedClickable(
                onClick = {
                    openNote(
                        note = note,
                        context = context
                    )
                },
                onLongClick = {
                    showDeleteDialog.value = true
                }
            ),
        shape = RoundedCornerShape(34.dp, 34.dp, 17.dp, 34.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(
            when (kotlin.math.abs(note.uuid.hashCode()) % 9) {
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
            text = note.getTitle(),
            fontFamily = lexendDeca,
            fontSize = 25.sp,
            lineHeight = 30.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
            modifier = Modifier.padding(10.dp, 0.dp, 5.dp, 0.dp),
        )
    }

    if (showDeleteDialog.value) {
        DeleteNoteDialog(
            onConfirm = {
                note.rm()
                showDeleteDialog.value = false
                onNoteDeleted()
            },
            onDismiss = {
                showDeleteDialog.value = false
            }
        )
    }
}

@Composable
private fun DeleteNoteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Delete note?") },
            confirmButton = {
            TextButton(onClick = onConfirm) {
                    Text("Delete")
                }
            },
            dismissButton = {
            TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
}

@Composable
private fun NotesListScreen(storage: page.newlevel.notes.storage.StorageAbstract, refreshKey: Int) {
    var localRefreshKey by remember { androidx.compose.runtime.mutableIntStateOf(0) }

    val (allNotes, _) = remember(refreshKey, localRefreshKey) {
        storage.listNotes(0, 100)
    }

    Column (
        modifier = Modifier.padding(2.dp)
    ) {
        allNotes.chunked(2).forEach { pair ->
            Row {
                pair.forEach { note ->
                    Note(
                        note = note,
                        modifier = Modifier.weight(1f),
                        onNoteDeleted = { localRefreshKey++ }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsBtn() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                android.widget.LinearLayout(ctx).apply {
                    orientation = android.widget.LinearLayout.HORIZONTAL
                    gravity = android.view.Gravity.CENTER_VERTICAL
                    setPadding(34, 34, 34, 34)
                    setBackground(GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        setColor(ctx.getColor(R.color.settings_btn_icon_bg))
                        setCornerRadius(ctx.resources.displayMetrics.widthPixels / 4f)
                    })
                    setOnClickListener { ctx.startActivity(android.content.Intent(ctx, SettingsActivity::class.java)) }

                    val iconView = android.widget.ImageView(ctx).apply {
                        setImageResource(R.drawable.settings)
                        layoutParams = android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, 0, 10, 0) // Margin between icon and text
                    }
                }

                    val textView = android.widget.TextView(ctx).apply {
                        text = ctx.getString(R.string.settings_title)
                        setTextColor(ctx.getColor(R.color.notes_foreground_text))
                        textSize = 18f
                        typeface = android.graphics.Typeface.create(androidx.core.content.res.ResourcesCompat.getFont(ctx, R.font.lexend_deca_normal), android.graphics.Typeface.BOLD)
                        setPadding(24, 0, 24, 0)
                        layoutParams = android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    }

                    addView(iconView)
                    addView(textView)
                }
            },
            modifier = Modifier.padding(10.dp)
        )
    }
}
