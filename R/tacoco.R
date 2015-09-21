library(car)
library(MASS)
library(RSQLite)  #install.packages("RSQLite")

#fetch data from SQLiteDB
SQLiteDriver <- dbDriver("SQLite")
TacocoDB <- dbConnect(SQLiteDriver,"tacoco.db")
fetchDB <- function(sql) {
  results <- dbSendQuery(TacocoDB, sql)
  fetch(results, n=-1)
}

#SQLs
SQL_NUMBER_OF_LINES_PER_TEST<-paste("SELECT COUNT(TEST_ID) V1 FROM LINE_COVERAGE GROUP BY TEST_ID")
SQL_NUMBER_OF_SRC_PER_TEST<-paste("SELECT COUNT(DISTINCT SOURCE_ID) V1 FROM LINE_COVERAGE GROUP BY TEST_ID")
SQL_SOURCE_TEST_PAIRS<-paste("SELECT SOURCE_ID, TEST_ID FROM LINE_COVERAGE GROUP BY SOURCE_ID, TEST_ID")


test = fetchDB(SQL_NUMBER_OF_LINES_PER_TEST)
hist((test$V1), xlab="Covered Lines", ylab="Number of Testcase", xlim=c(0,1000), main="Covered Lines Per Test Case");
hist(log(test$V1), xlab="log(Covered Lines)", ylab="Number of Testcase", main="Covered Lines Per Test Case");

test = fetchDB(SQL_NUMBER_OF_SRC_PER_TEST)
hist((test$V1), xlab="Number of Source Files", ylab="Number of Testcase", main="Covered Source files Per Test Case");
hist(log(test$V1), xlab="log(Covered Lines)", ylab="Number of Testcase", main="Covered Source files Per Test Case");

test = fetchDB(SQL_SOURCE_TEST_PAIRS)
scatterplot(y = test$SOURCE_ID, x = test$TEST_ID,  ylab="SOURCE_ID", xlab="TEST_ID", 
            main="Coverage", smoother=FALSE)#, cex.lab=1.5, cex.main = 1.5)
dbDisconnect(TacocoDB)
  