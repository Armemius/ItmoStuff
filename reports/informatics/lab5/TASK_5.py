import pandas as pd
import seaborn as sb
import matplotlib.pyplot as plot

df = pd.read_csv(r"C:\Users\rusgy\OneDrive\Рабочий стол\INF_LAB_5_TASK_PROCESSED.csv", sep=";", encoding="utf-8")
sb.boxplot(data=df, orient="v")
plot.xticks(rotation=45)
plot.show()

