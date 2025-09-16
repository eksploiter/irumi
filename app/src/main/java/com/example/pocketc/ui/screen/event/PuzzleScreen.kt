package com.example.pocketc.ui.screen.event

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.pocketc.model.event.Puzzle
import com.example.pocketc.model.event.PuzzleData
import com.example.pocketc.model.event.User

@Composable
fun PuzzleScreen(viewModel: PuzzleViewModel = viewModel()) {
    val context = LocalContext.current

    val puzzleDataState by viewModel.puzzleData.collectAsState()
    val puzzleImageUrl by viewModel.puzzleImageUrl.collectAsState()

    val placeholderBitmap = rememberPlaceholderBitmap()

    var bitmap by remember { mutableStateOf<Bitmap?>(placeholderBitmap) }

    LaunchedEffect(puzzleImageUrl) {
        bitmap = loadBitmapFromUrl(context, puzzleImageUrl, placeholderBitmap)
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 다른 UI 요소들 (ex: 상단바, 유저 정보, 랭킹 등)
            Text("퍼즐 이벤트")

            // 퍼즐 부분만 별도의 컴포넌트로 분리
            bitmap?.let { loadedBitmap ->
                puzzleDataState?.let { data ->
                    // 데이터가 로드된 경우 퍼즐 그리드 표시
                    PuzzleGrid(data, loadedBitmap)
                } ?: LoadingPlaceholder()
            } ?: LoadingPlaceholder()

            // 다른 UI 요소들 (ex: 랭킹 리스트)
            // RankingList()
        }
    }
}

// todo 다른 파일로 분리
@Composable
fun PuzzleGrid(puzzleData: PuzzleData, bitmap: Bitmap) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(puzzleData.puzzles.size) { index ->
            val puzzle = puzzleData.puzzles[index]
            val pieceBitmap = cropBitmapPiece(bitmap, puzzle.row, puzzle.column, 3, 3)
            PuzzleItem(puzzle, pieceBitmap) { pieceId ->
                // 클릭 이벤트
            }
        }
    }
}

fun rememberPlaceholderBitmap(): Bitmap {
    return createBitmap(100, 100).apply {
        eraseColor(android.graphics.Color.GRAY) // 간단한 회색 비트맵
    }
}

suspend fun loadBitmapFromUrl(context: Context, url: String?, placeholder: Bitmap): Bitmap {
    if (url.isNullOrEmpty()) return placeholder

    return try {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .build()
        val result = loader.execute(request)
        val drawable = (result as? SuccessResult)?.drawable
        if (drawable is BitmapDrawable) drawable.bitmap else placeholder
    } catch (e: Exception) {
        placeholder
    }
}

@Composable
fun LoadingPlaceholder() { // todo 진짜 로딩으로 바꾸기
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("퍼즐 이미지 로딩 중...")
    }
}

// todo 다른 파일로 분리
@Composable
fun PuzzleItem(
    puzzle: Puzzle,
    piece: ImageBitmap?,
    onPuzzleClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (puzzle.filledBy != null && piece != null) {
            Image(
                painter = BitmapPainter(piece),
                contentDescription = "퍼즐 조각",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
            )
        }
    }
}

// Bitmap 자르기
fun cropBitmapPiece(
    bitmap: Bitmap,
    row: Int,
    col: Int,
    totalRows: Int,
    totalCols: Int
): ImageBitmap {
    val pieceWidth = bitmap.width / totalCols
    val pieceHeight = bitmap.height / totalRows
    val x = (col - 1) * pieceWidth
    val y = (row - 1) * pieceHeight
    return Bitmap.createBitmap(bitmap, x, y, pieceWidth, pieceHeight).asImageBitmap()
}

// ---------------------------------
// Preview용 더미 화면
// ---------------------------------
@Preview(showBackground = true)
@Composable
fun PreviewPuzzleScreen() {
    val grayBitmap = remember {
        rememberPlaceholderBitmap()
    }

    val dummyPuzzles = List(9) { index ->
        Puzzle(
            pieceId = index + 1,
            row = (index / 3) + 1,
            column = (index % 3) + 1,
            filledBy = if (index % 2 == 0) User(1, "UserA") else null,
            filledAt = null
        )
    }
    val dummyPuzzleData =
        PuzzleData(puzzles = dummyPuzzles, totalPieces = 9, filledCount = 5)

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 다른 UI 요소들 (ex: 상단바, 유저 정보, 랭킹 등)
            Text("퍼즐 이벤트")

            PuzzleGrid(
                puzzleData = dummyPuzzleData,
                bitmap = grayBitmap
            )

            // 다른 UI 요소들 (ex: 랭킹 리스트)
            // RankingList()
        }
    }
}
