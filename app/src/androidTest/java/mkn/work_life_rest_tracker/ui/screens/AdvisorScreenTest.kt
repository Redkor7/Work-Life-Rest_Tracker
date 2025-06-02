package mkn.work_life_rest_tracker.ui.screens

import org.junit.Test
import org.junit.Assert.*

class AdvisorScreenTest {

    @Test
    fun generateRecommendationReturnsNoDataMessageWhenStatsAreZero() {
        val stats = Triple(0, 0, 0)
        val result = generateRecommendation(stats)
        assertEquals("Нет данных для анализа.", result)
    }

    @Test
    fun generateRecommendation_returns_perfect_balance_message_for_ideal_stats() {
        val stats = Triple(40, 50, 10)
        val result = generateRecommendation(stats)
        assertEquals("У вас идеальный баланс!", result)
    }

   @Test
    fun generateRecommendation_suggests_more_work_time_when_work_is_low() {
        val stats = Triple(10, 50, 10)
        val result = generateRecommendation(stats)
        assertTrue(result.contains("Тратьте больше времени на Work"))
    }

    @Test
    fun generateRecommendation_suggests_less_work_time_when_work_is_high() {
        val stats = Triple(70, 20, 5)
        val result = generateRecommendation(stats)
        assertTrue(result.contains("Тратьте меньше времени на Work"))
    }

    @Test
    fun generateRecommendation_suggests_more_life_time_when_life_is_low() {
        val stats = Triple(40, 20, 10)
        val result = generateRecommendation(stats)
        assertTrue(result.contains("Тратьте больше времени на Life"))
    }

    @Test
    fun generateRecommendation_suggests_more_rest_time_when_rest_is_low() {
        val stats = Triple(40, 50, 5)
        val result = generateRecommendation(stats)
        assertTrue(result.contains("Тратьте больше времени на Rest"))
    }

    @Test
    fun buildRecommendationText_creates_correct_message_for_low_work() {
        val result = buildRecommendationText(30, 50, 10, Triple(40, 50, 10))
        assertTrue(result.contains("Тратьте больше времени на Work: +2 ч в день"))
    }

    @Test
    fun buildRecommendationText_creates_correct_message_for_high_work() {
        val result = buildRecommendationText(50, 40, 10, Triple(40, 50, 10))
        assertTrue(result.contains("Тратьте меньше времени на Work: -2 ч в день"))
        assertTrue(result.contains("Тратьте больше времени на Life: +2 ч в день"))
    }

    @Test
    fun AdviceItem_data_class_works_correctly() {
        val item = AdviceItem("test", "Test Title", "Test Text")
        assertEquals("test", item.key)
        assertEquals("Test Title", item.title)
        assertEquals("Test Text", item.text)
    }
}