# PluralKitMC

PluralKitMC Allows you to interface with the PluralKit Discord bot. You can do this by loading your system id (found from pk;system) with /pk load <system id>, and then everything should work as usual! Typing in chat with your proxy tags will cause the message to be sent as the appropriate system member.

Commands:

    /pk help -- Lists the PluralKitMC commands
    /pk load <system id> -- Links you to the given system id
    /pk update -- Forces your system information to refresh
    /pk link <token> -- Links your account to the token from pk;token
    /pk unlink -- Removes the attached token
    /pk autoproxy <off/front/latch/member>-- Configures your autoproxy settings
    /pk switch [out/member...]-- Switch out or to one or more member
    /pk find <search term>-- Searches for a member by name
    /pk random-- Lists a random member from your system
    /pk member <member>-- Display information regarding a user in your system
    /pk system [list] [full]-- Display information regarding your system, or list its members

**Note: Database data storage is not yet supported.**