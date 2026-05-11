import traceback
import matplotlib.pyplot as plt
import sys
import pymysql
import traceback
import pandas as pd
import numpy as np
from common.Common import *
from matplotlib.patches import Patch
from common.lang.lang_loader import *

map_names_location = "maps.long."
map_types_location = "map_types."
chart_location = "statistics.friendly_team_wins.chart."
team_location = "teams."


def get_connection(host, user, password, database):
    conn = pymysql.connect(
        host=host,
        user=user,
        password=password,
        database=database
    )
    cursor = conn.cursor()

    return cursor


def get_map_data(host, user, password, map_data_database, table):
    conn = pymysql.connect(
        host=host,
        user=user,
        password=password,
        database=map_data_database
    )
    cursor = conn.cursor()

    cursor.execute(
        f"SELECT map, mode, SUM(CASE WHEN friendly_score > opponent_score THEN 1 ELSE 0 END) as wins, COUNT(*) as total FROM {table} GROUP BY map;")

    return cursor.fetchall()


def add_color_data_to_df(host, user, password, df, database, map_registry_table):
    conn = pymysql.connect(
        host=host,
        user=user,
        password=password,
        database=database
    )
    cursor = conn.cursor()

    cursor.execute(
        f"SELECT map_id, color from `{map_registry_table}`")

    color_data = cursor.fetchall()
    color_data = pd.DataFrame(color_data, columns=["map_id", "color"])
    return pd.merge(df, color_data, on="map_id")


def get_legend_from_df(df):
    unique_combinations = df[['color', 'mode_id']].drop_duplicates()

    legend_elements = []

    for index, row in unique_combinations.iterrows():
        map_mode_lang = get_value(map_types_location + row['mode_id'])
        legend_label = get_value(chart_location + "legend")

        formatted_label = legend_label.replace("{mode_id}", row['mode_id']).replace("{mode_name}", map_mode_lang)

        row_patch = Patch(facecolor=row['color'], edgecolor='grey', label=formatted_label)

        legend_elements.append(row_patch)

    total_maps_label = get_value(chart_location + "legend.maps_played")
    total_patch = Patch(facecolor='grey', edgecolor='grey', label=total_maps_label)

    legend_elements.append(total_patch)

    return legend_elements


def add_map_name_to_df(df):
    lang_df = pd.DataFrame(get_map_names(), columns=['map_id', 'map_name'])

    return df.merge(lang_df, on='map_id')


def get_chart_title(team_id):
    base_string = get_value(statistics_section + "title")
    team_name = get_value(team_location + team_id)

    return base_string.replace("{team_id}", team_id).replace("{team_name}", team_name)


def get_x_axis_name():
    return get_value(statistics_section + "x_axis_name")


def get_y_axis_name():
    return get_value(statistics_section + "y_axis_name")


def create_chart(output_path, host, user, password,
                 map_data_database, map_registry_database, map_data_table, map_registry_table, team_id):

    data = get_map_data(host, user, password, map_data_database, map_data_table)

    data = pd.DataFrame(data, columns=["map_id", "mode_id", "wins", "total"])
    data = data[~data['mode_id'].str.contains('submap', case=False, na=False)]

    data = add_color_data_to_df(host, user, password, data, map_registry_database, map_registry_table)
    data = add_map_name_to_df(data)

    data = data.sort_values(by='mode_id')

    r1 = np.arange(len(data))

    plt.figure(figsize=(10, 6))

    plt.bar(r1, data['total'], color='grey', width=0.5, edgecolor='grey', label='Maps Played')
    plt.bar(r1, data['wins'], color=data['color'], width=0.5, edgecolor='grey', label='wins')

    title = get_chart_title(team_id)
    x_axis_name = get_x_axis_name()
    y_axis_name = get_y_axis_name()

    plt.title(title)
    plt.xlabel(x_axis_name)
    plt.ylabel(y_axis_name)
    plt.xticks([r for r in range(len(data))], data['map_name'])
    plt.xticks(rotation=45)

    plt.grid(axis='y', linestyle='--', alpha=0.7)

    legend_elements = get_legend_from_df(data)

    plt.tight_layout()
    plt.legend(handles=legend_elements, loc='upper right')

    plt.savefig(f'{output_path}')


try:
    pass
    # create_chart("D:/analyst/Maryville/Astryx/Astryx Java/chart/chart.png", "localhost", "root", "",
    # "astryxfx_maps", "astryxfx_registry", "test_maps", "map_registry", "testing_environment")
except Exception as e:
    with open("D:/analyst/Maryville/Astryx/AstryxFX/logs/python.log", "w") as file:
        file.write(e.__str__())

toWrite = ""

try:
    with open("D:/analyst/Maryville/Astryx/AstryxFX/logs/python.log", "w") as file:
        toWrite += sys.argv.__str__() + "\n"

    create_chart(sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4],
                 sys.argv[5], sys.argv[6], sys.argv[7], sys.argv[8], sys.argv[9])
except Exception as e:
    stack_trace = traceback.format_exc()
    toWrite += stack_trace

with open("D:/analyst/Maryville/Astryx/AstryxFX/logs/python.log", "w") as file:
    file.write(toWrite)
