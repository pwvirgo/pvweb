select keyword1, keyword2, descrip, sum(amount) as cost, count(*)
from transact
group by keyword1, keyword2, descrip
order by sum(amount), keyword1, keyword2, descrip