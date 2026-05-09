package ru.sibfu.domain.usecase.categoryUseCase

import ru.sibfu.domain.CategoryModel
import ru.sibfu.domain.interfaces.ICategoryRepository
import javax.inject.Inject

class GetAllCategoryUseCase @Inject constructor(
    private val repository: ICategoryRepository
) {
    suspend operator fun invoke(): Result<List<CategoryModel>> {
        try {
            val data = repository.getAllCategory()
            return Result.success(data)
        } catch (e: Exception){
            return Result.failure(e)
        }
    }
}