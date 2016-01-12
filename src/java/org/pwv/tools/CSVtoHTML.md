#Parse CSV file and create an HTML table
-- a (selectable string) delimiter seperates columns within a row
-- every row ends with a new-line (including the last line in the file
-- a column may include new-lines if the column is enclosed in double quotes
-- the last column does not end with a delimiter but with a new-line or end-of-file
-- the first row contains headers for every column
-- columns containing new-lines, double-quotes, and commas should be enclosed in double-quotes
-- If double-quotes are used to enclose fields, then a double-quote appearing inside a field must be escaped by preceding it with another double quote

