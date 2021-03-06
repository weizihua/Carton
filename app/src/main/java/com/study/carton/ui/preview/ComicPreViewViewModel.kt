package com.study.carton.ui.preview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.study.carton.base.BaseViewModel
import com.study.carton.bean.detail.ComicDetailResponse
import com.study.carton.bean.preview.ComicPreViewResponse
import com.study.carton.db.BookDatabase
import com.study.carton.db.ReadChapter
import com.study.carton.http.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ComicPreViewViewModel : BaseViewModel() {
    val mPerViewResponse = MutableLiveData<ComicPreViewResponse>()
    private val chapterDao = BookDatabase.instance.getReadChapterDao()
    //当前最新章节信息
    private var mCurrRequestNewChapterBean: ComicDetailResponse.ChapterListBean? = null


    fun setCurrChapterInfo(currRequestNewChapterBean: ComicDetailResponse.ChapterListBean) {
        this.mCurrRequestNewChapterBean = currRequestNewChapterBean
    }


    fun getPreView(chapterId: String) {
        request({
            val comicPreView = RetrofitClient.service.comicPreView(chapterId)
            mCurrRequestNewChapterBean?.also {
                val imageList = comicPreView.data?.returnData?.image_list
                imageList?.forEachIndexed { index, imageListBean ->
                    imageListBean.chapterName = it.name
                    imageListBean.listIndex = index + 1
                    imageListBean.listSize = imageList.size
                    imageListBean.chapter_id = it.chapter_id
                }
                val chapter = it.chapter_id?.let { it1 -> chapterDao.getReadChapter(it1) }
                if (chapter == null) {
                    comicPreView.data?.returnData?.position = 0
                } else {
                    comicPreView.data?.returnData?.position = chapter.readPosition
                }

            }
            comicPreView
        }, {
            mPerViewResponse.value = it.returnData
        })
    }

    fun saveReadChapter(
        comicId: String?,
        comicName: String?,
        chapter_id: String,
        chapterName: String,
        type: String,
        readPosition: Int
    ): Boolean {
        if (comicId != null && comicName != null) {
            chapterDao.update(
                ReadChapter(
                    0,
                    chapter_id,
                    chapterName,
                    comicId,
                    comicName,
                    type,
                    readPosition
                )
            )
        }
        return true
    }


    fun saveReadPosition(chapter_id: String, position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val chapter = chapterDao.getReadChapter(chapter_id)

            if (chapter == null) {
                chapterDao.insert(ReadChapter(0,chapter_id,"","","","",position))

            } else {
                chapter.readPosition = position
                chapterDao.saveReadPosition(chapter)
            }

//            saveReadPosition(chapter_id, position)
//            com.orhanobut.logger.Logger.e("-- 保存阅读位置 $position status")
        }
    }

}