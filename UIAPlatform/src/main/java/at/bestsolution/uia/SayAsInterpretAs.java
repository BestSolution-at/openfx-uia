/*
 * -----------------------------------------------------------------
 * Copyright (c) 2021 BestSolution.at EDV Systemhaus GmbH
 * All Rights Reserved.
 *
 * BestSolution.at MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE  OR NON - INFRINGEMENT.
 * BestSolution.at SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS
 * SOFTWARE OR ITS DERIVATIVES.
 *
 * This software is released under the terms of the
 *
 *                  "GNU General Public License, Version 2
 *                         with classpath exception"
 *
 * and may only be distributed and used under the terms of the
 * mentioned license. You should have received a copy of the license
 * along with this software product, if not you can download it from
 * http://www.gnu.org/licenses/gpl.html
 * ----------------------------------------------------------------
 */
package at.bestsolution.uia;

import java.util.Optional;
import java.util.stream.Stream;

/** Defines the values that indicate how a text-to-speech engine should interpret specific data. */
public enum SayAsInterpretAs implements INativeEnum {
    /** The text should be spoken using the default for the text-to-speech engine. */
    None(0),
    /** The text should be spoken character by character. */
    Spell(1),
    /** The text is an integral or decimal number and should be spoken as a cardinal number. */
    Cardinal(2),
    /** The text is an integral number and should be spoken as an ordinal number. */
    Ordinal(3),
    /** The text should be spoken as a number. */
    Number(4),
    /** The text should be spoken as a date. */
    Date(5),
    /** The text should be spoken as a time value. */
    Time(6),
    /** The text should be spoken as a telephone number. */
    Telephone(7),
    /** The text should be spoken as currency. */
    Currency(8),
    /** The text should be spoken as a network address, including saying the '', '/', and '@' characters. */
    Net(9),
    /** The text should be spoken as a URL. */
    Url(10),
    /** The text should be spoken as an address. */
    Address(11),
    /** The text should be spoken as an alphanumeric number. */
    Alphanumeric(12),
    /** The text should be spoken as a name. */
    Name(13),
    /** The text should be spoken as media. */
    Media(14),
    /** The text should be spoken as a date in a Month/Day/Year format. */
    Date_MonthDayYear(15),
    /** The text should be spoken as a date in a Day/Month/Year format. */
    Date_DayMonthYear(16),
    /** The text should be spoken as a date in a Year/Month/Day format. */
    Date_YearMonthDay(17),
    /** The text should be spoken as a date in a Year/Month format. */
    Date_YearMonth(18),
    /** The text should be spoken as a date in a Month/Year format. */
    Date_MonthYear(19),
    /** The text should be spoken as a date in a Day/Month format. */
    Date_DayMonth(20),
    /** The text should be spoken as a date in a Month/Day format. */
    Date_MonthDay(21),
    /** The text should be spoken as a date in a Year format. */
    Date_Year(22),
    /** The text should be spoken as a time value in an Hours:Minutes:Seconds 12-hour format. */
    Time_HoursMinutesSeconds12(23),
    /** The text should be spoken as a time value in an Hours:Minutes 12-hour format. */
    Time_HoursMinutes12(24),
    /** The text should be spoken as a time value in an Hours:Minutes:Seconds 24-hour format. */
    Time_HoursMinutesSeconds24(25),
    /** The text should be spoken as a time value in an Hours:Minutes 24-hour format. */
    Time_HoursMinutes24(26);


    private final int value;
    private SayAsInterpretAs(int value) {
        this.value = value;
    }

    @Override
    public int getNativeValue() {
        return value;
    }

    @Override
    public String getConstantName() {
        return "SayAsInterpretAs_" + name();
    }


    public static Optional<SayAsInterpretAs> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
	}

}
