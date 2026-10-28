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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestYearMonthCalendar {
    private final Logger logger = LoggerFactory.getLogger(TestYearMonthCalendar.class);

    @BeforeAll
    public static void setUp() {
        // preload real holiday data so these tests don't depend on a live HTTP call
        TestHolidayData.preload();
    }

    @Test
    public void forwardMonthCalendarDay() {
        AdvanceCalendarInput calendarInput = new AdvanceCalendarInput();
        OutboundConnectorContext context = mock(OutboundConnectorContext.class);
        // make bindVariables return your prepared input
        when(context.bindVariables(AdvanceCalendarInput.class)).thenReturn(calendarInput);

        // populate your input
        calendarInput.calendarAdvanceFunction = DayFunction.ADVANCE_DAYS;
        calendarInput.startDate = "2026-06-14";
        calendarInput.direction = AdvanceCalendarInput.DIRECTION_V_FORWARD;
        calendarInput.dayProgression = AdvanceCalendarInput.DAY_PROGRESSION_V_CALENDARDAY;
        calendarInput.targetProgression = AdvanceCalendarInput.TARGET_PROGRESSION_AFTER;
        calendarInput.duration = "P1M";
        calendarInput.useHolidays = true;
        calendarInput.holidaysCountries = List.of("FR");
        calendarInput.businessCalendar = null;
        AdvanceCalendarFunction calendarFunction = new AdvanceCalendarFunction();
        try {
            AdvanceCalendarOutput advanceCalendarOutput = calendarFunction.execute(context);
            assert (advanceCalendarOutput.getNumberOfResultDate() == 1);
            for (AdvanceCalendarOutput.Result result : advanceCalendarOutput.getListResultDateCollection()) {
                logger.info("forwardMonthCalendarDay Result FoundDate:{} resultDate[{}] resultZonedDate[{}] periods[{}]",
                        result.foundDate,
                        result.resultDate.toLocalDate(),
                        result.resultZonedDate,
                        result.listPeriods.stream().map(Object::toString).collect(Collectors.joining(", ")));
                assertTrue(result.foundDate);
                assert (result.resultDate.toLocalDate().equals(LocalDate.of(2026, 7, 15)));
                assertNull(result.resultZonedDate);
            }
            logger.info("forwardMonthCalendarDay OK ");
        } catch (Exception e) {
            logger.error("forwardMonthCalendarDay", e);
            assert false;
        }
    }


    @Test
    public void backwardYearCalendarDay() {
        AdvanceCalendarInput calendarInput = new AdvanceCalendarInput();
        OutboundConnectorContext context = mock(OutboundConnectorContext.class);
        // make bindVariables return your prepared input
        when(context.bindVariables(AdvanceCalendarInput.class)).thenReturn(calendarInput);

        // populate your input
        calendarInput.calendarAdvanceFunction = DayFunction.ADVANCE_DAYS;
        calendarInput.startDate = "2026-12-25";
        calendarInput.direction = AdvanceCalendarInput.DIRECTION_V_BACKWARD;
        calendarInput.dayProgression = AdvanceCalendarInput.DAY_PROGRESSION_V_CALENDARDAY;
        calendarInput.targetProgression = AdvanceCalendarInput.TARGET_PROGRESSION_BEFORE;
        calendarInput.duration = "P2Y";
        calendarInput.useHolidays = true;
        calendarInput.holidaysCountries = List.of("FR");
        calendarInput.businessCalendar = null;
        AdvanceCalendarFunction calendarFunction = new AdvanceCalendarFunction();
        try {
            AdvanceCalendarOutput advanceCalendarOutput = calendarFunction.execute(context);
            assert (advanceCalendarOutput.getNumberOfResultDate() == 1);
            for (AdvanceCalendarOutput.Result result : advanceCalendarOutput.getListResultDateCollection()) {
                logger.info("backwardYearCalendarDay Result FoundDate:{} resultDate[{}] resultZonedDate[{}] periods[{}]",
                        result.foundDate,
                        result.resultDate.toLocalDate(),
                        result.resultZonedDate,
                        result.listPeriods.stream().map(Object::toString).collect(Collectors.joining(", ")));
                assertTrue(result.foundDate);
                assert (result.resultDate.toLocalDate().equals(LocalDate.of(2024, 12, 24)));
                assertNull(result.resultZonedDate);
            }
            logger.info("backwardYearCalendarDay OK ");
        } catch (Exception e) {
            logger.error("backwardYearCalendarDay", e);
            assert false;
        }
    }

    @Test
    public void forwardMonthAndDaysCalendarDay() {
        AdvanceCalendarInput calendarInput = new AdvanceCalendarInput();
        OutboundConnectorContext context = mock(OutboundConnectorContext.class);
        // make bindVariables return your prepared input
        when(context.bindVariables(AdvanceCalendarInput.class)).thenReturn(calendarInput);

        // populate your input
        calendarInput.calendarAdvanceFunction = DayFunction.ADVANCE_DAYS;
        calendarInput.startDate = "2026-06-14";
        calendarInput.direction = AdvanceCalendarInput.DIRECTION_V_FORWARD;
        calendarInput.dayProgression = AdvanceCalendarInput.DAY_PROGRESSION_V_CALENDARDAY;
        calendarInput.targetProgression = AdvanceCalendarInput.TARGET_PROGRESSION_BEFORE;
        calendarInput.duration = "P1M12DT10H30M"; // 14 june=>14 july + 12 J=> Sunday 26 july => Friday 24
        calendarInput.useHolidays = true;
        calendarInput.holidaysCountries = List.of("FR");
        calendarInput.businessCalendar = null;
        AdvanceCalendarFunction calendarFunction = new AdvanceCalendarFunction();
        try {
            AdvanceCalendarOutput advanceCalendarOutput = calendarFunction.execute(context);
            assert (advanceCalendarOutput.getNumberOfResultDate() == 1);
            for (AdvanceCalendarOutput.Result result : advanceCalendarOutput.getListResultDateCollection()) {

                logger.info("forwardMonthAndDaysCalendarDay Result FoundDate:{} resultDate[{}] resultZonedDate[{}] periods[{}]",
                        result.foundDate,
                        result.resultDate.toLocalDate(),
                        result.resultZonedDate,
                        result.listPeriods.stream().map(Object::toString).collect(Collectors.joining(", ")));
                assertTrue(result.foundDate);
                assert (result.resultDate.toLocalDate().equals(LocalDate.of(2026, 7, 24)));
                assertNull(result.resultZonedDate);
            }
            logger.info("forwardMonthAndDaysCalendarDay OK ");
        } catch (Exception e) {
            logger.error("forwardMonthAndDaysCalendarDay", e);
            assert false;
        }
    }

    @Test
    public void forwardBissextileAndDaysCalendarDay() {
        AdvanceCalendarInput calendarInput = new AdvanceCalendarInput();
        OutboundConnectorContext context = mock(OutboundConnectorContext.class);
        // make bindVariables return your prepared input
        when(context.bindVariables(AdvanceCalendarInput.class)).thenReturn(calendarInput);

        // populate your input
        calendarInput.calendarAdvanceFunction = DayFunction.ADVANCE_DAYS;
        calendarInput.startDate = "2024-02-29";
        calendarInput.direction = AdvanceCalendarInput.DIRECTION_V_FORWARD;
        calendarInput.dayProgression = AdvanceCalendarInput.DAY_PROGRESSION_V_CALENDARDAY;
        calendarInput.targetProgression = AdvanceCalendarInput.TARGET_PROGRESSION_RESULT;
        calendarInput.duration = "P1Y3D"; // 29 fev 2024=> 28 fev 2025+3d = 4 march
        calendarInput.useHolidays = false;
        calendarInput.businessCalendar = null;
        AdvanceCalendarFunction calendarFunction = new AdvanceCalendarFunction();
        try {
            AdvanceCalendarOutput advanceCalendarOutput = calendarFunction.execute(context);
            assert (advanceCalendarOutput.getNumberOfResultDate() == 1);
            for (AdvanceCalendarOutput.Result result : advanceCalendarOutput.getListResultDateCollection()) {
                logger.info("forwardMonthAndDaysCalendarDay Result FoundDate:{} resultDate[{}] resultZonedDate[{}] periods[{}]",
                        result.foundDate,
                        result.resultDate.toLocalDate(),
                        result.resultZonedDate,
                        result.listPeriods.stream().map(Object::toString).collect(Collectors.joining(", ")));
                assertTrue(result.foundDate);
                assert (result.resultDate.toLocalDate().equals(LocalDate.of(2025, 3, 3)));
                assertNull(result.resultZonedDate);
            }
            logger.info("forwardMonthAndDaysCalendarDay OK ");
        } catch (Exception e) {
            logger.error("forwardMonthAndDaysCalendarDay", e);
            assert false;
        }
    }
}
