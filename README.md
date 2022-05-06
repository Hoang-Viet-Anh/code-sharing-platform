# Code Sharing Platform

JetBrains Academy. Project: Code Sharing Platform

This project is created for sharing code. The endpoint for adding code to platform is <b>/code/new</b>.
There are 3 fields to fill. First field is field for code to share, second field is time restriction
which means time after which the code will not be available. Third field is views restriction that limits
the number of code views. If you do not need restrict code just leave the field empty.

After submitting code you will get identifier of your code by which you can get your code through endpoint
<b>/code/{id}</b>. Also you can get the last 10 added codes without restriction through endpoint <b>/code/latest</b>.

