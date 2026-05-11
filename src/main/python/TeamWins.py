import traceback
import matplotlib.pyplot as plt
import sys
import pymysql
import traceback
import pandas as pd
import numpy as np
import json
from common.Common import *
from matplotlib.patches import Patch
from common.lang.lang_loader import *

map_names_location = "maps.long."
map_types_location = "map_types."
chart_location = "statistics.team_wins.chart."


def get_connection(host, user, password, database):
    conn = pymysql.connect(
        host=host,
        user=user,
        password=password,
        database=database
    )
    cursor = conn.cursor()

    return cursor


def get_all_map_win_data(host, user, password, db, table, team_id, start_date, end_date):
    conn = pymysql.connect(
        host=host,
        user=user,
        password=password,
        database=db
    )
    cursor = conn.cursor()

    cursor.execute(
        f"SELECT `map`, COUNT(*) as count FROM `{table}`" +
        f"WHERE (`team1_id`='{team_id}' OR `team2_id`='{team_id}')" +
        f"AND `date`>='{start_date}' AND `date` <= '{end_date}' GROUP BY `map`"
    )

    return cursor.fetchall()


def get_map_win_data(host, user, password, db, table, team_id, start_date, end_date):
    conn = pymysql.connect(
        host=host,
        user=user,
        password=password,
        database=db
    )
    cursor = conn.cursor()

    cursor.execute(
        f"SELECT `map`, COUNT(*) as count FROM `{table}`" +
        f"WHERE `winner_id` = '{team_id}' " +
        f"AND `date`>='{start_date}' AND `date` <= '{end_date}' GROUP BY `map`"
    )

    return cursor.fetchall()


def get_map_ids(host, user, password, db, table):
    conn = pymysql.connect(
        host=host,
        user=user,
        password=password,
        database=db
    )
    cursor = conn.cursor()

    cursor.execute(
        f"SELECT * FROM {table}")

    return cursor.fetchall()


def get_map_registry(host, user, password, db, table):
    conn = pymysql.connect(
        host=host,
        user=user,
        password=password,
        database=db
    )
    cursor = conn.cursor()

    cursor.execute(
        f"SELECT * FROM {table}")

    return cursor.fetchall()


def get_map_id_and_color_from_name(registry_df, name):
    for index, (map_id, map_mode, color, valid_names, submap_parent_id) in registry_df.iterrows():
        valid_name_list = json.loads(valid_names)
        if name in valid_name_list:
            return map_id, map_mode, color

    return None, None, None


def add_map_data(map_registry_df, data_df):
    df = pd.DataFrame(columns=['map_id', 'mode_id', 'all_count', 'win_count', 'color'])

    for index, (guid, win_count, all_count, map_name) in data_df.iterrows():
        map_id, mode_id, color = get_map_id_and_color_from_name(map_registry_df, map_name)

        if map_id is None:
            df.loc[index] = [map_name, "", win_count, all_count, "grey"]

        df.loc[index] = [map_id, mode_id, win_count, all_count, color]

    return df


def get_legend_from_df(df):
    unique_combinations = df[['color', 'mode_id']].drop_duplicates()

    legend_elements = []

    row_patch = Patch(facecolor='grey', edgecolor='grey', label='All Maps')

    legend_elements.append(row_patch)

    for index, row in unique_combinations.iterrows():
        map_mode_lang = get_value(map_types_location + row['mode_id'])

        row_patch = Patch(facecolor=row['color'], edgecolor='grey', label=map_mode_lang)

        legend_elements.append(row_patch)

    return legend_elements


def add_map_name_to_df(df):
    lang_df = pd.DataFrame(get_map_names(), columns=['map_id', 'map_name'])

    return df.merge(lang_df, on='map_id')


def get_x_axis_name():
    return get_value(chart_location + "x_axis_name")


def get_y_axis_name():
    return get_value(chart_location + "y_axis_name")


def create_chart(output_path, host, user, password,
                 veto_database, veto_table,
                 map_registry_database, map_registry_table,
                 faceit_table_database, faceit_table,
                 team_id, team_name, start_date, end_date):
    map_win_data = get_map_win_data(host, user, password, veto_database, veto_table, team_id, start_date, end_date)
    map_win_data = pd.DataFrame(map_win_data, columns=["guid", "win_count"])

    all_win_data = get_all_map_win_data(host, user, password, veto_database, veto_table, team_id, start_date, end_date)
    all_win_data = pd.DataFrame(all_win_data, columns=["guid", "all_count"])

    data = pd.merge(map_win_data, all_win_data, how='outer')
    data.fillna(0, inplace=True)

    map_ids = get_map_ids(host, user, password, faceit_table_database, faceit_table)
    map_ids_df = pd.DataFrame(map_ids, columns=["guid", "map_name"])

    map_registry = get_map_registry(host, user, password, map_registry_database, map_registry_table)
    map_registry_df = pd.DataFrame(map_registry, columns=["map_id", "map_mode", "color", "valid_names", "submap_parent_id"])

    data = pd.merge(data, map_ids_df, on="guid", how="inner")
    data = add_map_data(map_registry_df, data)

    print(data)

    data = add_map_name_to_df(data)

    data = data.sort_values(by='mode_id')

    r1 = np.arange(len(data))

    plt.figure(figsize=(10, 6))

    plt.bar(r1, data['win_count'], color='grey', width=0.5, edgecolor='grey', label='total')
    plt.bar(r1, data['all_count'], color=data['color'], width=0.5, edgecolor='grey', label='wins')

    title = team_name + f" Maps ({start_date} TO {end_date})"
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

toWrite = ""

#create_chart("D:/analyst/Maryville/Astryx/AstryxFX/chart/wins.png",
#             "localhost", "root", "",
#             "astryxfx_data", "map_data",
#             "astryxfx_registry", "map_registry",
#             "astryxfx_data", "faceit_maps",
#             "d20d1868-8937-4a85-8706-bf936d265392", "FAGGOT TEAM",
#             "2024-08-20", "2024-09-20")

try:
    toWrite += sys.argv.__str__() + "\n"

    create_chart(sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4],
                 sys.argv[5], sys.argv[6], sys.argv[7], sys.argv[8],
                 sys.argv[9], sys.argv[10], sys.argv[11], sys.argv[12],
                 sys.argv[13], sys.argv[14])
except Exception as e:
    stack_trace = traceback.format_exc()
    toWrite += stack_trace

with open("D:/analyst/Maryville/Astryx/AstryxFX/logs/wins.log", "w") as file:
    file.write(toWrite)
