import configparser

file_path = "D:/analyst/Maryville/Astryx/AstryxFX/src/main/resources/com/kjersti/astryx/lang/en_us.properties"
maps_section = "maps.long."
map_types_section = "map_types."
statistics_section = "statistics.friendly_team_wins.chart."

config = configparser.ConfigParser()

with open(file_path) as f:
    file_content = '[DEFAULT]\n' + f.read()

config.read_string(file_content)


def get_value(key):
    return config.get('DEFAULT', key, fallback=key)


def get_map_names():
    map_names = []

    for item in config.items("DEFAULT"):
        if item[0].startswith(maps_section):
            index = item[0].index(maps_section)

            map_id = item[0][index + len(maps_section):]

            map_names.append((map_id, item[1]))

    return map_names