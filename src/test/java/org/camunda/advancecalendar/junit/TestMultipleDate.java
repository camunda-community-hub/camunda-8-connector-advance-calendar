package org.camunda.advancecalendar.junit;

import io.camunda.connector.api.outbound.OutboundConnectorContext;
import org.camunda.connector.advancecalendar.AdvanceCalendarFunction;
import org.camunda.connector.advancecalendar.AdvanceCalendarInput;
import org.camunda.connector.advancecalendar.AdvanceCalendarOutput;
import org.camunda.connector.advancecalendar.advanceday.DayFunction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestMultipleDate {
    private final Logger logger = LoggerFactory.getLogger(TestMultipleDate.class);

    @BeforeAll
    public static void setUp() {
        // preload real holiday data so these tests don't depend on a live HTTP call
        TestHolidayData.preload();
    }

    @Test
    public void multipleListDates() {
        AdvanceCalendarInput calendarInput = new AdvanceCalendarInput();
        OutboundConnectorContext context = mock(OutboundConnectorContext.class);
        // make bindVariables return your prepared input
        when(context.bindVariables(AdvanceCalendarInput.class)).thenReturn(calendarInput);

        // populate your input
        calendarInput.calendarAdvanceFunction = DayFunction.ADVANCE_DAYS;
        calendarInput.startDate = "2026-07-15";
        calendarInput.direction = AdvanceCalendarInput.DIRECTION_V_BACKWARD;
        calendarInput.dayProgression = AdvanceCalendarInput.DAY_PROGRESSION_V_CALENDARDAY;
        calendarInput.targetProgression = AdvanceCalendarInput.TARGET_PROGRESSION_AFTER;
        calendarInput.durations = List.of("P2D","P3D") ;
        calendarInput.useHolidays = true;
        calendarInput.holidaysCountries = List.of("FR");
        calendarInput.businessCalendar = null;
        AdvanceCalendarFunction calendarFunction = new AdvanceCalendarFunction();
        try {
            AdvanceCalendarOutput advanceCalendarOutput = calendarFunction.execute(context);
            assertEquals(Boolean.FALSE, advanceCalendarOutput.isResultIsAMap());

            assert (advanceCalendarOutput.getNumberOfResultDate() == 2);

            AdvanceCalendarOutput.Result result = advanceCalendarOutput.getListResultDate("0");
                logger.info("testBackCalendarDayOpen Result FoundDate:{} resultDate[{}] resultZonedDate[{}]", result.foundDate, result.resultDate.toLocalDate(), result.resultZonedDate);
                assertTrue(result.foundDate);
                assert (result.resultDate.toLocalDate().equals(LocalDate.of(2026, 7, 13)));
                assertNull(result.resultZonedDate);

             result = advanceCalendarOutput.getListResultDate("1");
            logger.info("testBackCalendarDayClosedAfterPolicy Result FoundDate:{} resultDate[{}] resultZonedDate[{}]",
                    result.foundDate,
                    result.resultDate.toLocalDate(),
                    result.resultZonedDate);

            assertTrue(result.foundDate);
            assert (result.resultDate.toLocalDate().equals(LocalDate.of(2026, 7, 13)));
            assertNull(result.resultZonedDate);


            logger.info("testBackCalendarDayOpen OK ");
        } catch (Exception e) {
            logger.error("testBackCalendarDayOpen", e);
            assert false;
        }
    }
    @Test
    public void multipleMapDates() {
        AdvanceCalendarInput calendarInput = new AdvanceCalendarInput();
        OutboundConnectorContext context = mock(OutboundConnectorContext.class);
        // make bindVariables return your prepared input
        when(context.bindVariables(AdvanceCalendarInput.class)).thenReturn(calendarInput);

        // populate your input
        calendarInput.calendarAdvanceFunction = DayFunction.ADVANCE_DAYS;
        calendarInput.startDate = "2026-07-15";
        calendarInput.direction = AdvanceCalendarInput.DIRECTION_V_BACKWARD;
        calendarInput.dayProgression = AdvanceCalendarInput.DAY_PROGRESSION_V_CALENDARDAY;
        calendarInput.targetProgression = AdvanceCalendarInput.TARGET_PROGRESSION_AFTER;
        calendarInput.durations = Map.of("2Days", "P2D","3Days", "P3D") ;
        calendarInput.useHolidays = true;
        calendarInput.holidaysCountries = List.of("FR");
        calendarInput.businessCalendar = null;
        AdvanceCalendarFunction calendarFunction = new AdvanceCalendarFunction();
        try {
            AdvanceCalendarOutput advanceCalendarOutput = calendarFunction.execute(context);
            assertEquals(Boolean.TRUE, advanceCalendarOutput.isResultIsAMap());

            assert (advanceCalendarOutput.getNumberOfResultDate() == 2);
            AdvanceCalendarOutput.Result result = advanceCalendarOutput.getListResultDate("2Days");
            logger.info("testBackCalendarDayOpen Result FoundDate:{} resultDate[{}] resultZonedDate[{}]", result.foundDate, result.resultDate.toLocalDate(), result.resultZonedDate);
            assertTrue(result.foundDate);
            assert (result.resultDate.toLocalDate().equals(LocalDate.of(2026, 7, 13)));
            assertNull(result.resultZonedDate);

            result = advanceCalendarOutput.getListResultDate("3Days");
            logger.info("testBackCalendarDayClosedAfterPolicy Result FoundDate:{} resultDate[{}] resultZonedDate[{}]",
                    result.foundDate,
                    result.resultDate.toLocalDate(),
                    result.resultZonedDate);

            assertTrue(result.foundDate);
            assert (result.resultDate.toLocalDate().equals(LocalDate.of(2026, 7, 13)));
            assertNull(result.resultZonedDate);


            logger.info("testBackCalendarDayOpen OK ");
        } catch (Exception e) {
            logger.error("testBackCalendarDayOpen", e);
            assert false;
        }
    }

}
