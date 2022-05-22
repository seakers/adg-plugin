import os
from distutils.dir_util import copy_tree



def run():
    print('--> UPDATING ADG PLUGIN')
    plugins_path = 'C:\\Program Files\\Cameo Systems Modeler Demo\\plugins\\adg'
    adg_plugin = 'C:\\Users\\apaza\\repos\\seakers\\adgplugin\\adg'
    copy_tree(adg_plugin, plugins_path)







if __name__ == "__main__":
    run()