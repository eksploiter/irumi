package com.example.pocketc.ui.screen.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketc.model.event.Puzzle
import com.example.pocketc.model.event.PuzzleData
import com.example.pocketc.model.event.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PuzzleViewModel : ViewModel() {

    private val _puzzleImageUrl = MutableStateFlow<String?>(null)
    val puzzleImageUrl: StateFlow<String?> =
        _puzzleImageUrl.asStateFlow()

    private val _puzzleData = MutableStateFlow<PuzzleData?>(null)
    val puzzleData: StateFlow<PuzzleData?> = _puzzleData.asStateFlow()

    // ViewModel 생성 시점에 호출
    init {
        getPuzzleImageUrl()
        getPuzzles()
    }

    fun getPuzzleImageUrl() {
        viewModelScope.launch {
            _puzzleImageUrl.value =
                "https://mblogthumb-phinf.pstatic.net/MjAyMzEwMDhfMjMz/MDAxNjk2NzMyNTA3NzM1.O5iVGUwOEGFbxoqzH9H5H2qwFmbLNdOR7PmuuNE2PMAg.eY7eLpHanrC_AWz-9T2VCZamarnMq_5i6MBHboR2Z1Ug.JPEG.qmfosej/IMG_7989.JPG?type=w800"
        }
    }

    // 퍼즐 데이터
    fun getPuzzles() {
        viewModelScope.launch {
            _puzzleData.value = PuzzleData(
                puzzles = listOf(
                    Puzzle(1, 1, 1, User(123, "절약왕"), null),
                    Puzzle(2, 1, 2, null, null),
                    Puzzle(3, 1, 3, User(124, "소비조절러"), null),
                    Puzzle(4, 2, 1, null, null),
                    Puzzle(5, 2, 2, User(124, "소비조절러"), null),
                    Puzzle(6, 2, 3, User(124, "소비조절러"), null),
                    Puzzle(7, 3, 1, null, null),
                    Puzzle(8, 3, 2, User(124, "소비조절러"), null),
                    Puzzle(9, 3, 3, null, null)
                ),
                totalPieces = 9,
                filledCount = 5
            )
        }
    }
}