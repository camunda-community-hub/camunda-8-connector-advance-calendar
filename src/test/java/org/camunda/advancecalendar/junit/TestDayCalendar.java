package org.camunda.advancecalendar.junit;

import io.camunda.connector.api.outbound.OutboundConnectorContext;
import org.camunda.connector.advancecalendar.AdvanceCalendarFunction;
import org.camunda.connector.advancecalendar.AdvanceCalendarInput;
import org.camunda.connector.advancecalendar.AdvanceCalendarOutput;
import org.camunda.connector.advancecalendar.advanceday.DayFunction;
import org.camunda.connector.advancecalendar.timemachine.SlotContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestDayCalendar {
    private final Logger logger = LoggerFactory.getLogger(TestDayCalendar.class);

    @BeforeAll
    public static void setUp() {
        // preload real holiday data so these tests don't depend on a live HTTP call
        TestHolidayData.preload();
    }

    @Test
    public void backBusinessDay() {
        AdvanceCalendarInput calendarInput = new AdvanceCalendarInput();
        OutboundConnectorContext context = mock(OutboundConnectorContext.class);
        // make bindVariables return your prepared input
        when(context.bindVariables(AdvanceCalendarInput.class)).thenReturn(calendarInput);

        // populate your input
        calendarInput.calendarAdvanceFunction = DayFunction.ADVANCE_DAYS;
        calendarInput.startDate = "2026-07-15";
        calendarInput.direction = AdvanceCalendarInput.DIRECTION_V_BACKWARD;
        calendarInput.dayProgression = AdvanceCalendarInput.DAY_PROGRESSION_V_BUSINESSDAY;
        calendarInput.targetProgression = AdvanceCalendarInput.TARGET_PROGRESSION_AFTER;
        calendarInput.duration = "P2D";
        calendarInput.useHolidays = true;
        calendarInput.holidaysCountries = List.of("FR");
        calendarInput.businessCalendar = null;
        AdvanceCalendarFunction calendarFunction = new AdvanceCalendarFunction();
        try {
            AdvanceCalendarOutput advanceCalendarOutput = calendarFunction.execute(context);
            assert (advanceCalendarOutput.getNumberOfResultDate() == 1);
            for (AdvanceCalendarOutput.Result result : advanceCalendarOutput.getListResultDateCollection()) {
                logger.info("backBusinessDay Result FoundDate:{} resultDate[{}] resultZonedDate[{}] periods[{}]",
                        result.foundDate,
                        result.resultDate.toLocalDate(),
                        result.resultZonedDate,
                        result.listPeriods.stream().map(Object::toString).collect(Collectors.joining(", ")));
                // Duration: 730 mn
                // Wednesday 15: 94mn
                // Tuesday 14 : holiday
                // Monday 13:  9h=> 540
                // Friday 10: 96 mn => 18:00-96=16:24
                assertTrue(result.foundDate);
                assert (result.resultDate.toLocalDate().equals(LocalDate.of(2026, 7, 10)));
                assertNull(result.resultZonedDate);
            }
            // Check that result are correct set too
            assertTrue(advanceCalendarOutput.isFoundDate());
            assert (advanceCalendarOutput.getResultDate().toLocalDate().equals(LocalDate.of(2026, 7, 10)));
            assertNull(advanceCalendarOutput.getResultZonedDate());
            logger.info("backBusinessDay OK ");

        } catch (Exception e) {
            logger.error("backBusinessDay", e);
            assert false;
        }
    }


    @Test
    public void advanceBusinessDay() {
        AdvanceCalendarInput calendarInput = new AdvanceCalendarInput();
        OutboundConnectorContext context = mock(OutboundConnectorContext.class);
        // make bindVariables return your prepared input
        when(context.bindVariables(AdvanceCalendarInput.class)).thenReturn(calendarInput);

        // populate your input
        calendarInput.calendarAdvanceFunction = DayFunction.ADVANCE_DAYS;
        calendarInput.startDate = "2026-07-09";
        calendarInput.direction = AdvanceCalendarInput.DIRECTION_V_FORWARD;
        calendarInput.dayProgression = AdvanceCalendarInput.DAY_PROGRESSION_V_BUSINESSDAY;
        calendarInput.targetProgression = AdvanceCalendarInput.TARGET_PROGRESSION_RESULT;
        calendarInput.duration = "P4D"; // Friday 12, Monday 13, Wed 15, Thrusday 16 
        calendarInput.useHolidays = true;
        calendarInput.holidaysCountries = List.of("FR");
        calendarInput.businessCalendar = null;
        AdvanceCalendarFunction calendarFunction = new AdvanceCalendarFunction();
        try {
            AdvanceCalendarOutput advanceCalendarOutput = calendarFunction.execute(context);
            assert (advanceCalendarOutput.getNumberOfResultDate() == 1);
            for (AdvanceCalendarOutput.Result result : advanceCalendarOutput.getListResultDateCollection()) {
                logger.info("advanceBusinessDay Result FoundDate:{} resultDate[{}] resultZonedDate[{}] period[{}]",
                        result.foundDate,
                        result.resultDate.toLocalDate(),
                        result.resultZonedDate,
                        result.listPeriods.stream().map(Object::toString).collect(Collectors.joining(", ")));
                assertTrue(result.foundDate);
                assert (result.resultDate.toLocalDate().equals(LocalDate.of(2026, 7, 16)));
                assertNull(result.resultZonedDate);
            }
            // Check that result are correct set too
            assertTrue(advanceCalendarOutput.isFoundDate());
            assert (advanceCalendarOutput.getResultDate().toLocalDate().equals(LocalDate.of(2026, 7, 16)));
            assertNull(advanceCalendarOutput.getResultZonedDate());

            logger.info("advanceBusinessDay OK ");
        } catch (Exception e) {
            logger.error("advanceBusinessDay", e);
            assert false;
        }
    }


    @Test
    public void advanceBusinessDay247() {
        AdvanceCalendarInput calendarInput = new AdvanceCalendarInput();
        OutboundConnectorContext context = mock(OutboundConnectorContext.class);
        // make bindVariables return your prepared input
        when(context.bindVariables(AdvanceCalendarInput.class)).thenReturn(calendarInput);

        // populate your input
        calendarInput.calendarAdvanceFunction = DayFunction.ADVANCE_DAYS;
        calendarInput.startDate = "2026-07-09";
        calendarInput.direction = AdvanceCalendarInput.DIRECTION_V_FORWARD;
        calendarInput.dayProgression = AdvanceCalendarInput.DAY_PROGRESSION_V_BUSINESSDAY;
        calendarInput.targetProgression = AdvanceCalendarInput.TARGET_PROGRESSION_RESULT;
        calendarInput.duration = "P5D"; // Friday 12, Monday 13, Wed 15, Thursday 16
        calendarInput.useHolidays = true;
        calendarInput.holidaysCountries = List.of("FR");
        calendarInput.businessCalendar = SlotContainer.CALENDAR_24_7;
        AdvanceCalendarFunction calendarFunction = new AdvanceCalendarFunction();
        try {
            AdvanceCalendarOutput advanceCalendarOutput = calendarFunction.execute(context);
            assert (advanceCalendarOutput.getNumberOfResultDate() == 1);
            for (AdvanceCalendarOutput.Result result : advanceCalendarOutput.getListResultDateCollection()) {
                logger.info("advanceBusinessDay Result FoundDate:{} resultDate[{}] resultZonedDate[{}] period[{}]",
                        result.foundDate,
                        result.resultDate.toLocalDate(),
                        result.resultZonedDate,
                        result.listPeriods.stream().map(Object::toString).collect(Collectors.joining(", ")));
                assertTrue(result.foundDate);
                assert (result.resultDate.toLocalDate().equals(LocalDate.of(2026, 7, 15)));
                assertNull(result.resultZonedDate);
            }
            // Check that result are correct set too
            assertTrue(advanceCalendarOutput.isFoundDate());
            assert (advanceCalendarOutput.getResultDate().toLocalDate().equals(LocalDate.of(2026, 7, 15)));
            assertNull(advanceCalendarOutput.getResultZonedDate());

            logger.info("advanceBusinessDay OK ");
        } catch (Exception e) {
            logger.error("advanceBusinessDay", e);
            assert false;
        }
    }


    @Test
    public void backCalendarDayOpen() {
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
        calendarInput.duration = "P2D"; // 15-2 = Monday 13
        calendarInput.useHolidays = true;
        calendarInput.holidaysCountries = List.of("FR");
        calendarInput.businessCalendar = null;
        AdvanceCalendarFunction calendarFunction = new AdvanceCalendarFunction();
        try {
            AdvanceCalendarOutput advanceCalendarOutput = calendarFunction.execute(context);
            assert (advanceCalendarOutput.getNumberOfResultDate() == 1);
            for (AdvanceCalendarOutput.Result result : advanceCalendarOutput.getListResultDateCollection()) {
                logger.info("testBackCalendarDayOpen Result FoundDate:{} resultDate[{}] resultZonedDate[{}]", result.foundDate, result.resultDate.toLocalDate(), result.resultZonedDate);
                assertTrue(result.foundDate);
                assert (result.resultDate.toLocalDate().equals(LocalDate.of(2026, 7, 13)));
                assertNull(result.resultZonedDate);
            }
            // Check that result are correct set too
            assertTrue(advanceCalendarOutput.isFoundDate());
            assert (advanceCalendarOutput.getResultDate().toLocalDate().equals(LocalDate.of(2026, 7, 13)));
            assertNull(advanceCalendarOutput.getResultZonedDate());

            logger.info("testBackCalendarDayOpen OK ");
        } catch (Exception e) {
            logger.error("testBackCalendarDayOpen", e);
            assert false;
        }
    }

    @Test
    public void backCalendarDayClosedBeforePolicy() {
        AdvanceCalendarInput calendarInput = new AdvanceCalendarInput();
        OutboundConnectorContext context = mock(OutboundConnectorContext.class);
        // make bindVariables return your prepared input
        when(context.bindVariables(AdvanceCalendarInput.class)).thenReturn(calendarInput);

        // populate your input
        calendarInput.calendarAdvanceFunction = DayFunction.ADVANCE_DAYS;
        calendarInput.startDate = "2026-07-15";
        calendarInput.direction = AdvanceCalendarInput.DIRECTION_V_BACKWARD;
        calendarInput.dayProgression = AdvanceCalendarInput.DAY_PROGRESSION_V_CALENDARDAY;
        calendarInput.targetProgression = AdvanceCalendarInput.TARGET_PROGRESSION_BEFORE;
        calendarInput.duration = "P3D"; // 15-3=Sunday 12 BEFORE => Friday 10
        calendarInput.useHolidays = true;
        calendarInput.holidaysCountries = List.of("FR");
        calendarInput.businessCalendar = null;
        AdvanceCalendarFunction calendarFunction = new AdvanceCalendarFunction();
        try {
            AdvanceCalendarOutput advanceCalendarOutput = calendarFunction.execute(context);
            assert (advanceCalendarOutput.getNumberOfResultDate() == 1);
            for (AdvanceCalendarOutput.Result result : advanceCalendarOutput.getListResultDateCollection()) {
                logger.info("testBackCalendarDayClosedAfterPolicy Result FoundDate:{} resultDate[{}] resultZonedDate[{}]",
                        result.foundDate,
                        result.resultDate.toLocalDate(),
                        result.resultZonedDate);

                assertTrue(result.foundDate);
                assert (result.resultDate.toLocalDate().equals(LocalDate.of(2026, 7, 10)));
                assertNull(result.resultZonedDate);
            }
            // Check that result are correct set too
            assertTrue(advanceCalendarOutput.isFoundDate());
            assert (advanceCalendarOutput.getResultDate().toLocalDate().equals(LocalDate.of(2026, 7, 10)));
            assertNull(advanceCalendarOutput.getResultZonedDate());

            logger.info("testBackCalendarDayClosedAfterPolicy OK ");
        } catch (Exception e) {
            logger.error("testBackCalendarDayClosedAfterPolicy", e);
            assert false;
        }
    }


    @Test
    public void backCalendarDayClosedAfterPolicy() {
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
        calendarInput.duration = "P3D"; // 15-3=Sun 12 - AFTER => Monday 13
        calendarInput.useHolidays = true;
        calendarInput.holidaysCountries = List.of("FR");
        calendarInput.businessCalendar = null;
        AdvanceCalendarFunction calendarFunction = new AdvanceCalendarFunction();
        try {
            AdvanceCalendarOutput advanceCalendarOutput = calendarFunction.execute(context);
            assert (advanceCalendarOutput.getNumberOfResultDate() == 1);
            for (AdvanceCalendarOutput.Result result : advanceCalendarOutput.getListResultDateCollection()) {
                logger.info("testBackCalendarDayClosedAfterPolicy Result FoundDate:{} resultDate[{}] resultZonedDate[{}] periods[{}]",
                        result.foundDate,
                        result.resultDate.toLocalDate(),
                        result.resultZonedDate,
                        result.listPeriods.stream().map(Object::toString).collect(Collectors.joining(", ")));

                assertTrue(result.foundDate);
                assert (result.resultDate.toLocalDate().equals(LocalDate.of(2026, 7, 13)));
                assertNull(result.resultZonedDate);
            }
            // Check that result are correct set too
            assertTrue(advanceCalendarOutput.isFoundDate());
            assert (advanceCalendarOutput.getResultDate().toLocalDate().equals(LocalDate.of(2026, 7, 13)));
            assertNull(advanceCalendarOutput.getResultZonedDate());

            logger.info("testBackCalendarDayClosedAfterPolicy OK ");
        } catch (Exception e) {
            logger.error("testBackCalendarDayClosedAfterPolicy", e);
            assert false;
        }
    }

    @Test
    public void backCalendarDayClosedResultPolicy() {
        AdvanceCalendarInput calendarInput = new AdvanceCalendarInput();
        OutboundConnectorContext context = mock(OutboundConnectorContext.class);
        // make bindVariables return your prepared input
        when(context.bindVariables(AdvanceCalendarInput.class)).thenReturn(calendarInput);

        // populate your input
        calendarInput.calendarAdvanceFunction = DayFunction.ADVANCE_DAYS;
        calendarInput.startDate = "2026-07-15";
        calendarInput.direction = AdvanceCalendarInput.DIRECTION_V_BACKWARD;
        calendarInput.dayProgression = AdvanceCalendarInput.DAY_PROGRESSION_V_CALENDARDAY;
        calendarInput.targetProgression = AdvanceCalendarInput.TARGET_PROGRESSION_RESULT;
        calendarInput.duration = "P3D"; // 15-3=Sunday 12
        calendarInput.useHolidays = true;
        calendarInput.holidaysCountries = List.of("FR");
        calendarInput.businessCalendar = null;
        AdvanceCalendarFunction calendarFunction = new AdvanceCalendarFunction();
        try {
            AdvanceCalendarOutput advanceCalendarOutput = calendarFunction.execute(context);
            assert (advanceCalendarOutput.getNumberOfResultDate() == 1);
            for (AdvanceCalendarOutput.Result result : advanceCalendarOutput.getListResultDateCollection()) {

                logger.info("testBackCalendarDayClosedAfterPolicy Result FoundDate:{} resultDate[{}] resultZonedDate[{}] periods[{}]",
                        result.foundDate,
                        result.resultDate.toLocalDate(),
                        result.resultZonedDate,
                        result.listPeriods.stream().map(Object::toString).collect(Collectors.joining(", ")));

                assertTrue(result.foundDate);
                assert (result.resultDate.toLocalDate().equals(LocalDate.of(2026, 7, 12)));
                assertNull(result.resultZonedDate);
            }
            // Check that result are correct set too
            assertTrue(advanceCalendarOutput.isFoundDate());
            assert (advanceCalendarOutput.getResultDate().toLocalDate().equals(LocalDate.of(2026, 7, 12)));
            assertNull(advanceCalendarOutput.getResultZonedDate());

            logger.info("testBackCalendarDayClosedAfterPolicy OK ");
        } catch (Exception e) {
            logger.error("testBackCalendarDayClosedAfterPolicy", e);
            assert false;
        }
    }

    @Test
    public void forwardCalendarDayOpen() {
        AdvanceCalendarInput calendarInput = new AdvanceCalendarInput();
        OutboundConnectorContext context = mock(OutboundConnectorContext.class);
        // make bindVariables return your prepared input
        when(context.bindVariables(AdvanceCalendarInput.class)).thenReturn(calendarInput);

        // populate your input
        calendarInput.calendarAdvanceFunction = DayFunction.ADVANCE_DAYS;
        calendarInput.startDate = "2026-07-10";
        calendarInput.direction = AdvanceCalendarInput.DIRECTION_V_FORWARD;
        calendarInput.dayProgression = AdvanceCalendarInput.DAY_PROGRESSION_V_CALENDARDAY;
        calendarInput.targetProgression = AdvanceCalendarInput.TARGET_PROGRESSION_AFTER;
        calendarInput.duration = "P3D";
        calendarInput.useHolidays = true;
        calendarInput.holidaysCountries = List.of("FR");
        calendarInput.businessCalendar = null;
        AdvanceCalendarFunction calendarFunction = new AdvanceCalendarFunction();
        try {
            AdvanceCalendarOutput advanceCalendarOutput = calendarFunction.execute(context);
            assert (advanceCalendarOutput.getNumberOfResultDate() == 1);
            for (AdvanceCalendarOutput.Result result : advanceCalendarOutput.getListResultDateCollection()) {

                logger.info("testForwardCalendarDayOpen Result FoundDate:{} resultDate[{}] resultZonedDate[{}] Periods[{}]",
                        result.foundDate,
                        result.resultDate.toLocalDate(),
                        result.resultZonedDate,
                        result.listPeriods.stream().map(Object::toString).collect(Collectors.joining(", ")));

                assertTrue(result.foundDate);
                assert (result.resultDate.toLocalDate().equals(LocalDate.of(2026, 7, 13)));
                assertNull(result.resultZonedDate);
            }
            // Check that result are correct set too
            assertTrue(advanceCalendarOutput.isFoundDate());
            assert (advanceCalendarOutput.getResultDate().toLocalDate().equals(LocalDate.of(2026, 7, 13)));
            assertNull(advanceCalendarOutput.getResultZonedDate());

            logger.info("testForwardCalendarDayOpen OK ");
        } catch (Exception e) {
            logger.error("testForwardCalendarDayOpen", e);
            assert false;
        }
    }

    @Test
    public void forwardCalendarDayClosedAfterPolicy() {
        AdvanceCalendarInput calendarInput = new AdvanceCalendarInput();
        OutboundConnectorContext context = mock(OutboundConnectorContext.class);
        // make bindVariables return your prepared input
        when(context.bindVariables(AdvanceCalendarInput.class)).thenReturn(calendarInput);

        // populate your input
        calendarInput.calendarAdvanceFunction = DayFunction.ADVANCE_DAYS;
        calendarInput.startDate = "2026-07-10";
        calendarInput.direction = AdvanceCalendarInput.DIRECTION_V_FORWARD;
        calendarInput.dayProgression = AdvanceCalendarInput.DAY_PROGRESSION_V_CALENDARDAY;
        calendarInput.targetProgression = AdvanceCalendarInput.TARGET_PROGRESSION_AFTER;
        calendarInput.duration = "P4D"; // 10+3=13 arrived July 14 : After==>15
        calendarInput.useHolidays = true;
        calendarInput.holidaysCountries = List.of("FR");
        calendarInput.businessCalendar = null;
        AdvanceCalendarFunction calendarFunction = new AdvanceCalendarFunction();
        try {
            AdvanceCalendarOutput advanceCalendarOutput = calendarFunction.execute(context);
            assert (advanceCalendarOutput.getNumberOfResultDate() == 1);
            for (AdvanceCalendarOutput.Result result : advanceCalendarOutput.getListResultDateCollection()) {

                logger.info("forwardCalendarDayClosedAfterPolicy Result FoundDate:{} resultDate[{}] resultZonedDate[{}] Periods[{}]",
                        result.foundDate,
                        result.resultDate.toLocalDate(),
                        result.resultZonedDate,
                        result.listPeriods.stream().map(Object::toString).collect(Collectors.joining(", ")));

                assertTrue(result.foundDate);
                assert (result.resultDate.toLocalDate().equals(LocalDate.of(2026, 7, 15)));
                assertNull(result.resultZonedDate);
            }
            // Check that result are correct set too
            assertTrue(advanceCalendarOutput.isFoundDate());
            assert (advanceCalendarOutput.getResultDate().toLocalDate().equals(LocalDate.of(2026, 7, 15)));
            assertNull(advanceCalendarOutput.getResultZonedDate());

            logger.info("forwardCalendarDayClosedAfterPolicy OK ");
        } catch (Exception e) {
            logger.error("testForwardCalendarDayClosedAfterPolicy", e);
            assert false;
        }
    }


    @Test
    public void forwardCalendarDayClosedBeforePolicy() {
        AdvanceCalendarInput calendarInput = new AdvanceCalendarInput();
        OutboundConnectorContext context = mock(OutboundConnectorContext.class);
        // make bindVariables return your prepared input
        when(context.bindVariables(AdvanceCalendarInput.class)).thenReturn(calendarInput);

        // populate your input
        calendarInput.calendarAdvanceFunction = DayFunction.ADVANCE_DAYS;
        calendarInput.startDate = "2026-07-10";
        calendarInput.direction = AdvanceCalendarInput.DIRECTION_V_FORWARD;
        calendarInput.dayProgression = AdvanceCalendarInput.DAY_PROGRESSION_V_CALENDARDAY;
        calendarInput.targetProgression = AdvanceCalendarInput.TARGET_PROGRESSION_BEFORE;
        calendarInput.duration = "P2D"; // arrived Sunday => Saturday => Back on Friday!
        calendarInput.useHolidays = true;
        calendarInput.holidaysCountries = List.of("FR");
        calendarInput.businessCalendar = null;
        AdvanceCalendarFunction calendarFunction = new AdvanceCalendarFunction();
        try {
            AdvanceCalendarOutput advanceCalendarOutput = calendarFunction.execute(context);
            assert (advanceCalendarOutput.getNumberOfResultDate() == 1);
            for (AdvanceCalendarOutput.Result result : advanceCalendarOutput.getListResultDateCollection()) {

                logger.info("forwardCalendarDayClosedBeforePolicy Result FoundDate:{} resultDate[{}] resultZonedDate[{}] Periods[{}]",
                        result.foundDate,
                        result.resultDate.toLocalDate(),
                        result.resultZonedDate,
                        result.listPeriods.stream().map(Object::toString).collect(Collectors.joining(", ")));

                assertTrue(result.foundDate);
                assert (result.resultDate.toLocalDate().equals(LocalDate.of(2026, 7, 10)));
                assertNull(result.resultZonedDate);
            }
            // Check that result are correct set too
            assertTrue(advanceCalendarOutput.isFoundDate());
            assert (advanceCalendarOutput.getResultDate().toLocalDate().equals(LocalDate.of(2026, 7, 10)));
            assertNull(advanceCalendarOutput.getResultZonedDate());

            logger.info("forwardCalendarDayClosedBeforePolicy OK ");
        } catch (Exception e) {
            logger.error("forwardCalendarDayClosedBeforePolicy", e);
            assert false;
        }
    }


    @Test
    public void forwardCalendarDayClosedResultPolicy() {
        AdvanceCalendarInput calendarInput = new AdvanceCalendarInput();
        OutboundConnectorContext context = mock(OutboundConnectorContext.class);
        // make bindVariables return your prepared input
        when(context.bindVariables(AdvanceCalendarInput.class)).thenReturn(calendarInput);

        // populate your input
        calendarInput.calendarAdvanceFunction = DayFunction.ADVANCE_DAYS;
        calendarInput.startDate = "2026-07-10";
        calendarInput.direction = AdvanceCalendarInput.DIRECTION_V_FORWARD;
        calendarInput.dayProgression = AdvanceCalendarInput.DAY_PROGRESSION_V_CALENDARDAY;
        calendarInput.targetProgression = AdvanceCalendarInput.TARGET_PROGRESSION_RESULT;
        calendarInput.duration = "P2D"; // arrived Sunday => Saturday 12
        calendarInput.useHolidays = true;
        calendarInput.holidaysCountries = List.of("FR");
        calendarInput.businessCalendar = null;
        AdvanceCalendarFunction calendarFunction = new AdvanceCalendarFunction();
        try {
            AdvanceCalendarOutput advanceCalendarOutput = calendarFunction.execute(context);
            assert (advanceCalendarOutput.getNumberOfResultDate() == 1);
            for (AdvanceCalendarOutput.Result result : advanceCalendarOutput.getListResultDateCollection()) {
                logger.info("forwardCalendarDayClosedResultPolicy Result FoundDate:{} resultDate[{}] resultZonedDate[{}] Periods[{}]",
                        result.foundDate,
                        result.resultDate.toLocalDate(),
                        result.resultZonedDate,
                        result.listPeriods.stream().map(Object::toString).collect(Collectors.joining(", ")));

                assertTrue(result.foundDate);
                assert (result.resultDate.toLocalDate().equals(LocalDate.of(2026, 7, 12)));
                assertNull(result.resultZonedDate);
            }
            // Check that result are correct set too
            assertTrue(advanceCalendarOutput.isFoundDate());
            assert (advanceCalendarOutput.getResultDate().toLocalDate().equals(LocalDate.of(2026, 7, 12)));
            assertNull(advanceCalendarOutput.getResultZonedDate());

            logger.info("forwardCalendarDayClosedResultPolicy OK ");
        } catch (Exception e) {
            logger.error("forwardCalendarDayClosedResultPolicy", e);
            assert false;
        }
    }

    @Test
    public void backWeekCalendarDay() {
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
        calendarInput.duration = "P1W"; //15 - 1W => July 8
        calendarInput.useHolidays = true;
        calendarInput.holidaysCountries = List.of("FR");
        calendarInput.businessCalendar = null;
        AdvanceCalendarFunction calendarFunction = new AdvanceCalendarFunction();
        try {
            AdvanceCalendarOutput advanceCalendarOutput = calendarFunction.execute(context);
            assert (advanceCalendarOutput.getNumberOfResultDate() == 1);
            for (AdvanceCalendarOutput.Result result : advanceCalendarOutput.getListResultDateCollection()) {
                logger.info("backWeekBusinessDay Result FoundDate:{} resultDate[{}] resultZonedDate[{}] Periods[{}]",
                        result.foundDate,
                        result.resultDate.toLocalDate(),
                        result.resultZonedDate,
                        result.listPeriods.stream().map(Object::toString).collect(Collectors.joining(", ")));
                assertTrue(result.foundDate);
                assert (result.resultDate.toLocalDate().equals(LocalDate.of(2026, 7, 8)));
                assertNull(result.resultZonedDate);
            }
            // Check that result are correct set too
            assertTrue(advanceCalendarOutput.isFoundDate());
            assert (advanceCalendarOutput.getResultDate().toLocalDate().equals(LocalDate.of(2026, 7, 8)));
            assertNull(advanceCalendarOutput.getResultZonedDate());

            logger.info("backWeekBusinessDay OK ");
        } catch (Exception e) {
            logger.error("backWeekBusinessDay", e);
            assert false;
        }
    }

    @Test
    public void advanceWeekCalendarDay() {
        AdvanceCalendarInput calendarInput = new AdvanceCalendarInput();
        OutboundConnectorContext context = mock(OutboundConnectorContext.class);
        // make bindVariables return your prepared input
        when(context.bindVariables(AdvanceCalendarInput.class)).thenReturn(calendarInput);

        // populate your input
        calendarInput.calendarAdvanceFunction = DayFunction.ADVANCE_DAYS;
        calendarInput.startDate = "2026-07-15";
        calendarInput.direction = AdvanceCalendarInput.DIRECTION_V_FORWARD;
        calendarInput.dayProgression = AdvanceCalendarInput.DAY_PROGRESSION_V_CALENDARDAY;
        calendarInput.targetProgression = AdvanceCalendarInput.TARGET_PROGRESSION_AFTER;
        calendarInput.duration = "P1W"; //15 + 1W => July 22
        calendarInput.useHolidays = true;
        calendarInput.holidaysCountries = List.of("FR");
        calendarInput.businessCalendar = null;
        AdvanceCalendarFunction calendarFunction = new AdvanceCalendarFunction();
        try {
            AdvanceCalendarOutput advanceCalendarOutput = calendarFunction.execute(context);
            assert (advanceCalendarOutput.getNumberOfResultDate() == 1);
            for (AdvanceCalendarOutput.Result result : advanceCalendarOutput.getListResultDateCollection()) {
                logger.info("backWeekBusinessDay Result FoundDate:{} resultDate[{}] resultZonedDate[{}], periods[{}]",
                        result.foundDate,
                        result.resultDate.toLocalDate(),
                        result.resultZonedDate,
                        result.listPeriods.stream().map(Object::toString).collect(Collectors.joining(", ")));
                assertTrue(result.foundDate);
                assert (result.resultDate.toLocalDate().equals(LocalDate.of(2026, 7, 22)));
                assertNull(result.resultZonedDate);
            }
            // Check that result are correct set too
            assertTrue(advanceCalendarOutput.isFoundDate());
            assert (advanceCalendarOutput.getResultDate().toLocalDate().equals(LocalDate.of(2026, 7, 22)));
            assertNull(advanceCalendarOutput.getResultZonedDate());

            logger.info("backWeekBusinessDay OK ");
        } catch (Exception e) {
            logger.error("backWeekBusinessDay", e);
            assert false;
        }
    }


}