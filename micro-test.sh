#!/bin/bash

methodNames=(
    "org/apache/commons/validator/routines/BigDecimalValidator.isInRange(Ljava/math/BigDecimal;DD)Z"
    "org/apache/commons/validator/routines/checkdigit/CUSIPCheckDigit.toInt(CII)I"
    "org/apache/commons/validator/CreditCardValidator.luhnCheck(Ljava/lang/String;)Z"
    "org/apache/commons/validator/CreditCardValidator.isValid(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/checkdigit/ISINCheckDigit.calculateModulus(Ljava/lang/String;Z)I"
    "org/apache/commons/validator/routines/checkdigit/SedolCheckDigit.calculateModulus(Ljava/lang/String;Z)I"
    "org/apache/commons/validator/routines/BigIntegerValidator.validate(Ljava/lang/String;Ljava/lang/String;)Ljava/math/BigInteger;"
    "org/apache/commons/validator/routines/AbstractCalendarValidator.compareTime(Ljava/util/Calendar;Ljava/util/Calendar;I)I"
    "org/apache/commons/validator/routines/RegexValidator.toString()Ljava/lang/String;"
    "org/apache/commons/validator/UrlValidator.isValidQuery(Ljava/lang/String;)Z"
    #"org/apache/commons/validator/routines/IBANValidator.createValidators([Lorg/apache/commons/validator/routines/IBANValidator$Validator;)Ljava/util/concurrent/ConcurrentMap;"
    "org/apache/commons/validator/routines/TimeValidator.compareHours(Ljava/util/Calendar;Ljava/util/Calendar;)I"
    #"org/apache/commons/validator/routines/IBANValidator$Validator.getRegexValidator()Lorg/apache/commons/validator/routines/RegexValidator;"
    "org/apache/commons/validator/routines/ISSNValidator.isValid(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/ISBNValidator.validateISBN10(Ljava/lang/String;)Ljava/lang/String;"
    "org/apache/commons/validator/routines/checkdigit/ISBNCheckDigit.isValid(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/FloatValidator.validate(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Float;"
    "org/apache/commons/validator/routines/checkdigit/VerhoeffCheckDigit.isValid(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/checkdigit/CUSIPCheckDigit.weightedValue(III)I"
    "org/apache/commons/validator/routines/CalendarValidator.adjustToTimeZone(Ljava/util/Calendar;Ljava/util/TimeZone;)V"
    "org/apache/commons/validator/routines/DomainValidator.isValidGenericTld(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/AbstractFormatValidator.format(Ljava/lang/Object;Ljava/text/Format;)Ljava/lang/String;"
    "org/apache/commons/validator/routines/LongValidator.validate(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Long;"
    "org/apache/commons/validator/routines/AbstractCalendarValidator.calculateCompareResult(Ljava/util/Calendar;Ljava/util/Calendar;I)I"
    "org/apache/commons/validator/routines/DomainValidator.isValidLocalTld(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/AbstractNumberValidator.getFormat(Ljava/lang/String;Ljava/util/Locale;)Ljava/text/Format;"
    "org/apache/commons/validator/UrlValidator.isValidScheme(Ljava/lang/String;)Z"
    "org/apache/commons/validator/util/Flags.getFlags()J"
    "org/apache/commons/validator/routines/AbstractCalendarValidator.compare(Ljava/util/Calendar;Ljava/util/Calendar;I)I"
    #"org/apache/commons/validator/CreditCardValidator$Visa.matches(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/CurrencyValidator.parse(Ljava/lang/String;Ljava/text/Format;)Ljava/lang/Object;"
    "org/apache/commons/validator/UrlValidator.isValid(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/AbstractCalendarValidator.compareQuarters(Ljava/util/Calendar;Ljava/util/Calendar;I)I"
    "org/apache/commons/validator/routines/AbstractCalendarValidator.format(Ljava/lang/Object;Ljava/lang/String;Ljava/util/Locale;)Ljava/lang/String;"
    #"org/apache/commons/validator/routines/IBANValidator.getDefaultValidators()[Lorg/apache/commons/validator/routines/IBANValidator$Validator;"
    "org/apache/commons/validator/routines/CodeValidator.getMinLength()I"
    "org/apache/commons/validator/routines/DoubleValidator.validate(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Double;"
    "org/apache/commons/validator/routines/checkdigit/LuhnCheckDigit.weightedValue(III)I"
    "org/apache/commons/validator/routines/BigIntegerValidator.validate(Ljava/lang/String;)Ljava/math/BigInteger;"
    "org/apache/commons/validator/routines/ISBNValidator.validateISBN13(Ljava/lang/String;)Ljava/lang/String;"
    "org/apache/commons/validator/routines/ISBNValidator.validate(Ljava/lang/String;)Ljava/lang/String;"
    "org/apache/commons/validator/routines/AbstractNumberValidator.parse(Ljava/lang/String;Ljava/lang/String;Ljava/util/Locale;)Ljava/lang/Object;"
    "org/apache/commons/validator/routines/LongValidator.validate(Ljava/lang/String;)Ljava/lang/Long;"
    "org/apache/commons/validator/routines/checkdigit/ModulusCheckDigit.isValid(Ljava/lang/String;)Z"
    "org/apache/commons/validator/GenericValidator.minLength(Ljava/lang/String;II)Z"
    "org/apache/commons/validator/routines/DomainValidator.isValidDomainSyntax(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/TimeValidator.validate(Ljava/lang/String;Ljava/lang/String;)Ljava/util/Calendar;"
    "org/apache/commons/validator/util/Flags.toString()Ljava/lang/String;"
    "org/apache/commons/validator/routines/IBANValidator.hasValidator(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/checkdigit/SedolCheckDigit.weightedValue(III)I"
    "org/apache/commons/validator/routines/BigDecimalValidator.minValue(Ljava/math/BigDecimal;D)Z"
    "org/apache/commons/validator/routines/AbstractFormatValidator.format(Ljava/lang/Object;)Ljava/lang/String;"
    "org/apache/commons/validator/routines/checkdigit/IBANCheckDigit.isValid(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/AbstractCalendarValidator.getFormat(Ljava/lang/String;Ljava/util/Locale;)Ljava/text/Format;"
    "org/apache/commons/validator/routines/CalendarValidator.validate(Ljava/lang/String;)Ljava/util/Calendar;"
    "org/apache/commons/validator/routines/AbstractNumberValidator.maxValue(Ljava/lang/Number;Ljava/lang/Number;)Z"
    "org/apache/commons/validator/routines/RegexValidator.isValid(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/CodeValidator.getCheckDigit()Lorg/apache/commons/validator/routines/checkdigit/CheckDigit;"
    "org/apache/commons/validator/routines/DoubleValidator.validate(Ljava/lang/String;)Ljava/lang/Double;"
    #"org/apache/commons/validator/routines/CreditCardValidator.validLength(ILorg/apache/commons/validator/routines/CreditCardValidator$CreditCardRange;)Z"
    #"org/apache/commons/validator/CreditCardValidator.addAllowedCardType(Lorg/apache/commons/validator/CreditCardValidator$CreditCardType;)V"
    "org/apache/commons/validator/routines/DomainValidator.arrayContains([Ljava/lang/String;Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/DomainValidator.isValidCountryCodeTld(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/CreditCardValidator.genericCreditCardValidator(II)Lorg/apache/commons/validator/routines/CreditCardValidator;"
    "org/apache/commons/validator/routines/TimeValidator.validate(Ljava/lang/String;)Ljava/util/Calendar;"
    "org/apache/commons/validator/UrlValidator.isValidPath(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/CreditCardValidator.isOn(JJ)Z"
    "org/apache/commons/validator/routines/EmailValidator.getInstance(Z)Lorg/apache/commons/validator/routines/EmailValidator;"
    "org/apache/commons/validator/routines/BigDecimalValidator.maxValue(Ljava/math/BigDecimal;D)Z"
    "org/apache/commons/validator/routines/checkdigit/ISBN10CheckDigit.toInt(CII)I"
    "org/apache/commons/validator/routines/DomainValidator.isValidTld(Ljava/lang/String;)Z"
    #"org/apache/commons/validator/CreditCardValidator$Mastercard.matches(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/CreditCardValidator.isValid(Ljava/lang/String;)Z"
    "org/apache/commons/validator/GenericValidator.adjustForLineEnding(Ljava/lang/String;I)I"
    "org/apache/commons/validator/GenericValidator.isBlankOrNull(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/EmailValidator.isValidUser(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/AbstractNumberValidator.getFormat(Ljava/util/Locale;)Ljava/text/Format;"
    "org/apache/commons/validator/routines/CalendarValidator.compareWeeks(Ljava/util/Calendar;Ljava/util/Calendar;)I"
    #"org/apache/commons/validator/routines/IBANValidator.getValidator(Ljava/lang/String;)Lorg/apache/commons/validator/routines/IBANValidator$Validator;"
    "org/apache/commons/validator/Arg.setPosition(I)V"
    "org/apache/commons/validator/routines/CodeValidator.getRegexValidator()Lorg/apache/commons/validator/routines/RegexValidator;"
    "org/apache/commons/validator/routines/ISSNValidator.validate(Ljava/lang/String;)Ljava/lang/Object;"
    "org/apache/commons/validator/routines/ISBNValidator.getInstance(Z)Lorg/apache/commons/validator/routines/ISBNValidator;"
    "org/apache/commons/validator/routines/InetAddressValidator.isValid(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/AbstractNumberValidator.minValue(Ljava/lang/Number;Ljava/lang/Number;)Z"
    "org/apache/commons/validator/routines/ByteValidator.validate(Ljava/lang/String;)Ljava/lang/Byte;"
    "org/apache/commons/validator/routines/ISBNValidator.isValidISBN13(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/CodeValidator.getMaxLength()I"
    "org/apache/commons/validator/routines/AbstractFormatValidator.isValid(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/DomainValidator.chompLeadingDot(Ljava/lang/String;)Ljava/lang/String;"
    "org/apache/commons/validator/routines/ShortValidator.validate(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Short;"
    "org/apache/commons/validator/routines/checkdigit/ISBN10CheckDigit.weightedValue(III)I"
    "org/apache/commons/validator/routines/AbstractNumberValidator.isAllowFractions()Z"
    "org/apache/commons/validator/routines/ISSNValidator.validateEan(Ljava/lang/String;)Ljava/lang/Object;"
    "org/apache/commons/validator/GenericValidator.maxLength(Ljava/lang/String;II)Z"
    "org/apache/commons/validator/routines/DomainValidator.isValidInfrastructureTld(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/AbstractFormatValidator.isValid(Ljava/lang/String;Ljava/util/Locale;)Z"
    "org/apache/commons/validator/routines/IntegerValidator.validate(Ljava/lang/String;)Ljava/lang/Integer;"
    "org/apache/commons/validator/ISBNValidator.isValid(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/AbstractFormatValidator.format(Ljava/lang/Object;Ljava/lang/String;Ljava/util/Locale;)Ljava/lang/String;"
    #"org/apache/commons/validator/routines/CreditCardValidator$1.validate(Ljava/lang/String;)Ljava/lang/String;"
    "org/apache/commons/validator/UrlValidator.countToken(Ljava/lang/String;Ljava/lang/String;)I"
    "org/apache/commons/validator/routines/ISBNValidator.isValidISBN10(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/ISINValidator.isValid(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/AbstractCalendarValidator.parse(Ljava/lang/String;Ljava/lang/String;Ljava/util/Locale;Ljava/util/TimeZone;)Ljava/lang/Object;"
    "org/apache/commons/validator/routines/AbstractNumberValidator.isValid(Ljava/lang/String;Ljava/lang/String;Ljava/util/Locale;)Z"
    "org/apache/commons/validator/Arg.setName(Ljava/lang/String;)V"
    "org/apache/commons/validator/routines/RegexValidator.validate(Ljava/lang/String;)Ljava/lang/String;"
    "org/apache/commons/validator/routines/ISSNValidator.convertToEAN13(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
    "org/apache/commons/validator/routines/checkdigit/ModulusTenCheckDigit.weightedValue(III)I"
    "org/apache/commons/validator/routines/checkdigit/ModulusTenCheckDigit.isValid(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/AbstractFormatValidator.parse(Ljava/lang/String;Ljava/text/Format;)Ljava/lang/Object;"
    "org/apache/commons/validator/routines/checkdigit/IBANCheckDigit.calculate(Ljava/lang/String;)Ljava/lang/String;"
    "org/apache/commons/validator/util/Flags.clear()V"
    #"org/apache/commons/validator/routines/DomainValidator.getTLDEntries(Lorg/apache/commons/validator/routines/DomainValidator$ArrayType;)[Ljava/lang/String;"
    "org/apache/commons/validator/routines/PercentValidator.parse(Ljava/lang/String;Ljava/text/Format;)Ljava/lang/Object;"
    "org/apache/commons/validator/routines/AbstractCalendarValidator.format(Ljava/lang/Object;Ljava/text/Format;)Ljava/lang/String;"
    "org/apache/commons/validator/routines/InetAddressValidator.isValidInet6Address(Ljava/lang/String;)Z"
    #"org/apache/commons/validator/routines/CreditCardValidator.createRangeValidator([Lorg/apache/commons/validator/routines/CreditCardValidator$CreditCardRange;Lorg/apache/commons/validator/routines/checkdigit/CheckDigit;)Lorg/apache/commons/validator/routines/CodeValidator;"
    "org/apache/commons/validator/util/Flags.turnOnAll()V"
    #"org/apache/commons/validator/CreditCardValidatorTest$DinersClub.matches(Ljava/lang/String;)Z"
    "org/apache/commons/validator/util/Flags.isOff(J)Z"
    "org/apache/commons/validator/routines/BigDecimalValidator.validate(Ljava/lang/String;)Ljava/math/BigDecimal;"
    "org/apache/commons/validator/routines/CalendarValidator.compareDates(Ljava/util/Calendar;Ljava/util/Calendar;)I"
    "org/apache/commons/validator/routines/ShortValidator.validate(Ljava/lang/String;)Ljava/lang/Short;"
    "org/apache/commons/validator/routines/DomainValidator.isValid(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/checkdigit/ModulusCheckDigit.toInt(CII)I"
    "org/apache/commons/validator/routines/checkdigit/ModulusCheckDigit.calculate(Ljava/lang/String;)Ljava/lang/String;"
    "org/apache/commons/validator/routines/TimeValidator.validate(Ljava/lang/String;Ljava/util/Locale;)Ljava/util/Calendar;"
    "org/apache/commons/validator/routines/DateValidator.validate(Ljava/lang/String;)Ljava/util/Date;"
    "org/apache/commons/validator/routines/checkdigit/ISBN10CheckDigit.toCheckDigit(I)Ljava/lang/String;"
    "org/apache/commons/validator/routines/checkdigit/ISBNCheckDigit.calculate(Ljava/lang/String;)Ljava/lang/String;"
    "org/apache/commons/validator/routines/ISBNValidator.isValid(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/EmailValidator.isValid(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/EmailValidator.isValidDomain(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/DomainValidator.isOnlyASCII(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/RegexValidator.match(Ljava/lang/String;)[Ljava/lang/String;"
    "org/apache/commons/validator/routines/TimeValidator.compareSeconds(Ljava/util/Calendar;Ljava/util/Calendar;)I"
    "org/apache/commons/validator/routines/TimeValidator.compareTime(Ljava/util/Calendar;Ljava/util/Calendar;)I"
    "org/apache/commons/validator/routines/checkdigit/ISSNCheckDigit.weightedValue(III)I"
    "org/apache/commons/validator/routines/AbstractNumberValidator.isInRange(Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;)Z"
    #"org/apache/commons/validator/CreditCardValidator$Discover.matches(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/checkdigit/ModulusCheckDigit.calculateModulus(Ljava/lang/String;Z)I"
    "org/apache/commons/validator/routines/AbstractFormatValidator.format(Ljava/lang/Object;Ljava/util/Locale;)Ljava/lang/String;"
    "org/apache/commons/validator/routines/EmailValidator.getInstance(ZZ)Lorg/apache/commons/validator/routines/EmailValidator;"
    "org/apache/commons/validator/UrlValidator.isValidFragment(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/CalendarValidator.compareQuarters(Ljava/util/Calendar;Ljava/util/Calendar;)I"
    "org/apache/commons/validator/routines/CalendarValidator.compareMonths(Ljava/util/Calendar;Ljava/util/Calendar;)I"
    "org/apache/commons/validator/routines/InetAddressValidator.isValidInet4Address(Ljava/lang/String;)Z"
    "org/apache/commons/validator/routines/AbstractCalendarValidator.getFormat(Ljava/util/Locale;)Ljava/text/Format;"
    "org/apache/commons/validator/routines/checkdigit/ABANumberCheckDigit.weightedValue(III)I"
    "org/apache/commons/validator/util/Flags.turnOffAll()V"
    "org/apache/commons/validator/routines/IntegerValidator.validate(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Integer;"
    "org/apache/commons/validator/routines/DomainValidator.getInstance(ZLjava/util/List;)Lorg/apache/commons/validator/routines/DomainValidator;"
    "org/apache/commons/validator/routines/CalendarValidator.compareYears(Ljava/util/Calendar;Ljava/util/Calendar;)I"
    "org/apache/commons/validator/routines/AbstractNumberValidator.getFormatType()I"
    "org/apache/commons/validator/routines/IBANValidator.isValid(Ljava/lang/String;)Z"
)

for methodName in "${methodNames[@]}"; do
    echo "-Dtacoco.analyzer=org.spideruci.tacoco.analysis.MicroTestAnalyzer
    -Dtacoco.analyzer.mut=$methodName" > /tmp/microharness-analyzer.config && \
    mvn exec:java -Plauncher \
    -Dtacoco.sut=/Users/vpalepu/git/program-execution-artifacts/programs/commons-validator \
    -Dtacoco.home=/Users/vpalepu/git/tacoco \
    -Dtacoco.project=commons-validator \
    -Danalyzer.opts="/tmp/microharness-analyzer.config" | awk '/Starting analysis/{flag=1; next} /Finishing analysis/{flag=0} flag'
done





























































































































































